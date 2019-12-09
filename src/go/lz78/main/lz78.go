package main

import (
	"bytes"
	"errors"
	"fmt"
	"io"
	"math/bits"
	"os"
	"strconv"
	"time"
)

// todo do not write empty phrase in file

type phrase struct {
	link     int
	data     byte
	fullData []byte
}

var (
	dictionary                        []phrase
	dictionaryTail                    []byte = nil
	isContainsTime, bufIsContainsTime int64
)

func main() {
	beginTime := time.Now().UnixNano()
	isContainsTime = beginTime

	sourceFileName := "./probs1_generated.txt"
	resultFileName := "./probs1_compressed.txt"
	unzippedFileName := "./probs1_unzipped.txt"

	result, err := os.Create(resultFileName)
	if err != nil {
		panic(err)
	}

	archiveFile(&sourceFileName, *result)
	_ = result.Close()

	unzippedFile, err := os.Create(unzippedFileName)
	result, err = os.Open(resultFileName)
	if err != nil {
		panic(err)
	}
	unzipFile(*result, *unzippedFile)

	bufIsContainsTime = time.Now().UnixNano()
	fmt.Println("Time spent on execution:", (bufIsContainsTime-beginTime)/1000000,
		"ms Is contains time:", (bufIsContainsTime-isContainsTime)/1000000)

	fmt.Println("File comparison:", fileEquals(sourceFileName, unzippedFileName))
}

func archiveFile(sourceName *string, result os.File) {
	sourceFile, err := os.Open(*sourceName)
	if err != nil {
		panic(errors.New("error reading source file"))
	}

	var (
		currentSeq                         []byte
		readed, containsId, prevContainsId int
		isContains                         bool
	)
	arr := make([]byte, 64)
	dictionary = make([]phrase, 0)
	dictionary = append(dictionary, phrase{
		0, 0, []byte{},
	})

	for {
		readed, err = sourceFile.Read(arr)
		if err == io.EOF || readed == 0 {
			if len(currentSeq) > 0 {
				dictionaryTail = currentSeq
				fmt.Println("Dictionary tail:", string(dictionaryTail), " ", dictionaryTail)
			} else {
				fmt.Println("Dictionary tail empty")
			}
			break
		}
		for i := 0; i < readed; i++ {
			currentSeq = append(currentSeq, arr[i])
			isContains, containsId = isDictionaryContains(&currentSeq)
			if !isContains {
				dictionary = append(dictionary, phrase{
					link:     prevContainsId,
					data:     arr[i],
					fullData: currentSeq,
				})
				currentSeq = []byte{}
			}
			prevContainsId = containsId
		}
	}

	fmt.Println("Phrases total amount:", len(dictionary))

	err = sourceFile.Close()
	if err != nil {
		fmt.Println("file already been closed")
	}

	fmt.Print("dictionary size:")
	var size = 0
	for i := range dictionary {
		size += dictionary[i].sizeBytes()
	}
	fmt.Println(size, " bytes")

	writeDictionary(result, &dictionary)
}

func (p *phrase) sizeBytes() int {
	return len(p.fullData) + 12
}

// returns i number of phrase in dictionary, 0 if !contains
func isDictionaryContains(arr *[]byte) (bool, int) {
	for i := 0; i < len(dictionary); i++ {
		if bytes.Equal(dictionary[i].fullData, *arr) {
			return true, i
		}
	}
	return false, 0
}

func writeDictionary(result os.File, phrases *[]phrase) {
	var (
		buf           uint64 = 0b0
		cutMask       uint64 = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000
		cutedByte            = []byte{0}
		freeBitInBuf  byte   = 64
		linkBitSize          = byte(bits.Len32(uint32(len(dictionary) - 1)))
		phraseBitSize        = linkBitSize + 8
		dictionaryLen = uint64(len(dictionary))
	)
	// first byte of compressed file - size of link
	fmt.Println("Link size:", linkBitSize, "PhraseBitSize:", phraseBitSize)
	_, _ = result.Write([]byte{linkBitSize})
	// next, 8 bytes is dictionary size
	for i := 0; i < 8; i++ {
		cutedByte[0] = byte((dictionaryLen & cutMask) >> 56)
		_, _ = result.Write(cutedByte)
		dictionaryLen <<= 8
	}
	// and writing all the dictionary phrases sequentially, link -> data -> link -> data ...
	for i := 0; i < len(*phrases); i++ {
		// append new phrase to buf
		buf |= ((uint64((*phrases)[i].link) << 8) | (uint64((*phrases)[i].data))) << (freeBitInBuf - phraseBitSize)
		freeBitInBuf -= phraseBitSize
		for (64-freeBitInBuf)/8 > 0 { // while buf contains integral byte, cut it and write to file
			cutedByte[0] = byte((buf & cutMask) >> 56)
			_, _ = result.Write(cutedByte)
			buf <<= 8
			freeBitInBuf += 8
		}
	}
	if freeBitInBuf != 64 {
		for i := 0; i < len(dictionaryTail); i++ {
			buf |=  uint64(dictionaryTail[i]) << (56 - (64 - freeBitInBuf))
			cutedByte[0] = byte((cutMask & buf) >> 56)
			_, _ = result.Write(cutedByte)
			buf <<= 8
		}
		_, _ = result.Write([]byte{byte((buf & cutMask) >> 56)})
	} else {
		_, _ = result.Write(dictionaryTail)
	}
	fmt.Println("Rest bits in buf while writing:", 64-freeBitInBuf)
}

func unzipFile(archived, unzipped os.File) {
	var (
		linkBitSize, phraseBitSize, bitsInBuf, cutedData byte
		byteReaded, cutedLink                            int
		buf                                              uint64 = 0b0
		cutDataMask                                      uint64 = 0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000
		cutLinkMask                                      uint64 = 0b0
		phrases                                                 = make([]phrase, 0)
		dictionaryLen uint64 = 0
		err error
	)
	// reading link length byte
	linkArrSize := []byte{0}
	_, err = archived.Read(linkArrSize)
	if err != nil {
		panic(err)
	}
	linkBitSize = linkArrSize[0]
	phraseBitSize = linkBitSize + 8
	// reading dictionary size
	dictionaryLenArr := make([]byte, 8)
	_, err = archived.Read(dictionaryLenArr)
	if err != nil {
		panic(err)
	}
	var shift byte = 56
	for i := 0; i < 8; i++ {
		dictionaryLen |= uint64(dictionaryLenArr[i]) << shift
		shift -= 8
	}
	// calculating necessary mask for cutting link
	for i := byte(0); i < linkBitSize; i++ {	// todo funcGetLinkMask
		cutLinkMask <<= 1
		cutLinkMask |= 1
	}
	cutLinkMask <<= 64 - linkBitSize
	// reading all phrases
	bitsInBuf = 0
	bufArr := make([]byte, 64)

	dt := dictionaryTail
	fmt.Println(len(dt))

	outer:for {
		byteReaded, err = archived.Read(bufArr)
		if err == io.EOF || byteReaded == 0 {	// мб не нужно
			fmt.Printf("resting in buf: %d, buf: %064b\n", bitsInBuf, buf)
			break outer
		}
		for i := 0; i < byteReaded; i++ {
			buf |= uint64(bufArr[i]) << (56 - bitsInBuf)
			bitsInBuf += 8
			if bitsInBuf >= phraseBitSize {
				if dictionaryLen == 0 {	// if all phrases written
					//var restBitBeforeTail = bitsInBuf
					var b byte
					for e := i; e < byteReaded; e++ {
						b = byte((buf & cutDataMask) >> 56)
						buf <<= 8
						_, err = unzipped.Write([]byte{b})
						buf |= uint64(bufArr[i]) << (56 - bitsInBuf)
					}
					break outer
				}
				cutedLink = int((buf & cutLinkMask) >> (64 - linkBitSize))
				buf <<= linkBitSize
				cutedData = byte((buf & cutDataMask) >> 56)
				buf <<= 8
				p := phrase{
					link:     cutedLink,
					data:     cutedData,
					fullData: nil,
				}
				phrases = append(phrases, p)
				// todo concurrent writing (chan), all phrases too big for ram
				_, err = unzipped.Write(constructPhrase(&phrases, len(phrases)-1))
				if err != nil {
					panic(err)
				}
				dictionaryLen--
				bitsInBuf -= phraseBitSize
			}
		}
	}
}

func constructPhrase(phrases *[]phrase, id int) []byte {
	if id == 0 {
		return nil
	}
	if (*phrases)[id].link == 0 {
		return []byte{(*phrases)[id].data}
	}
	p := make([]byte, 0)
	for i := id; i != 0; i = (*phrases)[i].link {
		p = append([]byte{(*phrases)[i].data}, p...)
	}
	return p
}

func printDictionary(phrases *[]phrase) {
	fmt.Println("Dictionary:")
	for i := 0; i < len(*phrases); i++ {
		fmt.Println(i, string((*phrases)[i].fullData), (*phrases)[i].fullData, (*phrases)[i].link, (*phrases)[i].data)
	}
}

func (p *phrase) toString() string {
	return fmt.Sprintf("%s  |  %s", p.fullData, strconv.Itoa(p.link))
}
