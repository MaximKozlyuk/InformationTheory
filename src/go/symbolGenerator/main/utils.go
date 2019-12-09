package main

import (
	"encoding/csv"
	"fmt"
	"io/ioutil"
	"math/big"
	"math/rand"
	"os"
)

var (
	closeFile = func(file os.File) {
		if err := file.Close(); err != nil {
			fmt.Println("Error! file already closed!\n", err)
		}
	}
)

func readCSVFile(fileName *string) ([][]string, error) {
	file, err := os.Open(*fileName)
	if err != nil {
		return nil, err
	}
	defer func() {
		_ = file.Close()
	}()
	var (
		reader = csv.NewReader(file)
		data   [][]string
	)
	data, err = reader.ReadAll()
	if err != nil {
		return nil, err
	}
	return data, nil
}

func readSampleFile(fileName *string) []byte {
	file, err := os.Open(*fileName)
	if err != nil {
		panic(err)
	}
	defer func() {
		_ = file.Close()
	}()
	data, err := ioutil.ReadAll(file)
	if err != nil {
		panic(err)
	}
	return data
}

func convertProbabilities(p [][]string) {
	nonDependChances = make([]big.Float, len(p[1]))
	for i := range p[1] {
		convertedVal, _, err := big.ParseFloat(p[1][i], 10, 65, big.AwayFromZero)
		if err != nil {
			panic(err)
		}
		nonDependChances[i] = *convertedVal
	}
}

func convertDependProbabilities(p [][]string) {
	dependChances = make([][]big.Float, len(p)-1)
	for i := 1; i < len(p); i++ {
		dependChances[i-1] = make([]big.Float, len(p[i]))
	}
	for i := 1; i < len(p); i++ {
		for j := range p[i] {
			convertedVal, _, err := big.ParseFloat(p[i][j], 10, 65, big.AwayFromZero)
			if err != nil {
				panic(err)
			}
			dependChances[i-1][j] = *convertedVal
		}
	}
}

// сортировка входных данных по возрастанию вероятности появления символа
func quickSort(a []big.Float, s []string) ([]big.Float, []string) {
	if len(a) < 2 {
		return a, s
	}
	left, right := 0, len(a)-1
	pivot := rand.Int() % len(a)
	a[pivot], a[right] = a[right], a[pivot]
	for i := range a {
		if a[i].Cmp(&a[right]) == -1 {
			a[left], a[i] = a[i], a[left]
			s[left], s[i] = s[i], s[left]
			left++
		}
	}
	a[left], a[right] = a[right], a[left]
	s[left], s[right] = s[right], s[left]
	quickSort(a[:left], s[:left])
	quickSort(a[left+1:], s[left+1:])
	return a, s
}

func bigFloatArrToFloatArr(big []big.Float) []float64 {
	floatArr := make([]float64, len(big))
	for i := range big {
		floatArr[i], _ = big[i].Float64()
	}
	return floatArr
}
