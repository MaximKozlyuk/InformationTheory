package main

import (
	"fmt"
	"io"
	"os"
)

func fileEquals(fileName1, fileName2 string) bool {
	var (
		err    error
		readed int
	)
	f1, err := os.Open(fileName1)
	if err != nil {
		panic(err)
	}
	f2, err := os.Open(fileName2)
	if err != nil {
		panic(err)
	}
	defer func() {
		_ = f1.Close()
		_ = f2.Close()
	}()

	buf1 := make([]byte, 64)
	buf2 := make([]byte, 64)
	var counter = 0
	var equals = true
	for {
		readed, err = f1.Read(buf1)
		_, err = f2.Read(buf2)
		if err == io.EOF || readed == 0 {
			return equals
		}
		for i := 0; i < readed; i++ {
			if buf1[i] != buf2[i] {
				fmt.Println("not equals on byte:", counter)
				fmt.Println(buf1)
				fmt.Println(buf2)
				equals = false
			}
			counter++
		}
	}
	return equals
}
