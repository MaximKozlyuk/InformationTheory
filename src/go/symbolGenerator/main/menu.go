package main

import (
	"fmt"
	"os"
)

func menuRoot() {
	var command int
	for {
		fmt.Println("enter number of command:\n1)Generate sample file\n" +
			"2)Calculate probabilities from existing file\n3)Exit")
		_, err := fmt.Scanf("%d", &command)
		if err != nil {
			fmt.Println("Error!")
			continue
		}
		if command == 1 {
			sampleGenerationMenu()
		} else if command == 2 {
			calculateSampleProbabilitiesMenu()
		} else if command == 3 {
			os.Exit(0)
		} else {
			fmt.Println("No such command, try again")
			continue
		}
	}
}

func sampleGenerationMenu() {
	var err error
	isSquare := readFileWithSymbolsSet()
	fmt.Println("Initial data:")
	for i := range symbols {
		fmt.Print(symbols[i], " ")
	}

	fmt.Print("\nEnter size in symbols of generated file: ")
	_, err = fmt.Scanf("%d", &size)
	if err != nil {
		panic(err)
	}

	fmt.Println("Enter result file name:")
	var resultFileName string
	_, _ = fmt.Scanf("%s", &resultFileName)
	resultFile, err := os.Create(resultFileName)
	if err != nil {
		panic(err)
	}
	_ = resultFile.Truncate(1)
	defer closeFile(*resultFile)
	if isSquare {
		generateAndWriteDependSample(resultFile)
	} else {
		generateAndWriteSample(resultFile)
	}
}

func readFileWithSymbolsSet() bool {
	isSquare := false
	fmt.Print("Enter file with symbols set:")
	var fileName string
	for {
		_, err := fmt.Scanf("%s", &fileName)
		if err != nil {
			fmt.Println("error scanning file name, try again")
			continue
		}
		break
	}
	probabilities, err := readCSVFile(&fileName)
	if err != nil {
		fmt.Println("Error during reading csv file")
		return isSquare
	}

	symbols = probabilities[0]
	if len(probabilities) > 2 {
		convertDependProbabilities(probabilities)
		checkDependProbabilities()
		isSquare = true
	} else {
		convertProbabilities(probabilities)
		err = checkSumProbabilities()
		nonDependChances, symbols = quickSort(nonDependChances, symbols)
	}
	probabilities = nil
	return isSquare
}

func calculateSampleProbabilitiesMenu() {
	var resultFileName string
	fmt.Println("Enter name of sample file:")
	fmt.Scanf("%s", &resultFileName)
	data := readSampleFile(&resultFileName)
	if size == 0 {
		size = int64(len(data) - 1)
	}
	resultProbabilities, err := calcResultProbabilities(data)
	if err != nil {
		fmt.Println(err.Error())
		return
	}
	fmt.Println("size= ", size)
	fmt.Println("initial and result probabilities:")
	if len(nonDependChances) == 0 || nonDependChances == nil {
		for i := range dependChances {
			fmt.Print("[ ")
			for j := range dependChances[i] {
				fmt.Print(dependChances[i][j].String(), " ")
			}
			fmt.Println("]")
		}
	} else {
		fmt.Print("[ ")
		for i := range nonDependChances {
			fmt.Print(nonDependChances[i].String(), " ")
		}
		fmt.Println("]")
	}
	fmt.Println(resultProbabilities)
	if len(nonDependChances) == 0 || nonDependChances == nil {
		fmt.Println("1)H(x)= ", nonDependEntropy(bigFloatArrToFloatArr(dependChances[0])))
		fmt.Println("2)H(x)= ", nonDependEntropy(resultProbabilities))
	} else {
		fmt.Println("1)H(x)= ", nonDependEntropy(bigFloatArrToFloatArr(nonDependChances)))
		fmt.Println("2)H(x)= ", nonDependEntropy(resultProbabilities))
	}
}