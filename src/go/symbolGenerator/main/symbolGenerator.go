package main

import (
	"bufio"
	"bytes"
	"errors"
	"fmt"
	"math"
	"math/big"
	"math/rand"
	"os"
	"strconv"
	"time"
)

var (
	symbols          []string
	nonDependChances []big.Float
	dependChances    [][]big.Float
	sums             []float64
	rand_max         int32
	size             int64
	randomVal        = big.NewFloat(0)

	generatedSuccessfully = func(buf *bytes.Buffer, writer *bufio.Writer) {
		_, _ = writer.WriteString(buf.String())
		_ = writer.Flush()
		buf.Reset()
		fmt.Println("sample file generated successfully!")
	}
)

func init() {
	//a := make([][]float64, 4)
	//for i := range a {
	//	a[i] = make([]float64, 4, 4)
	//}
	//a[0][0] = 1
	//a[0][1] = 2
	//a[0][2] = 3
	//a[0][3] = 4
	//a[1][0] = 2
	//a[1][1] = 3
	//a[1][2] = 4
	//a[1][3] = 8
	//a[2][0] = 5
	//a[2][1] = 6
	//a[2][2] = 7
	//a[2][3] = 8
	//a[3][0] = 4
	//a[3][1] = 5
	//a[3][2] = 7
	//a[3][3] = 3
	//
	//b := make([]float64, 4)
	//b[3] = 1
	//
	//x := make([]float64, 4)
	//
	//x = cholesky(&a,&b)
	//fmt.Println(x)

	//arr := [][]string {{"","",""},{"",""}}
	//fmt.Println(len(arr), " ", len(arr[0]), " ", len(arr[1]), "\n then\n")
	//
	//arr2 := make([][]int, 10)
	//fmt.Println(len(arr2), len(arr[0]))
	//
	//for i := range arr2 {
	//	fmt.Println(len(arr2[i]))
	//}
	//
	//os.Exit(0)
}

func main() {
	fmt.Println("Symbol generator")
	rand_max = 2 // на 1 больше чем число для вхождения в диапазон,  [0,1]
	menuRoot()
}

func nonDependEntropy(probabilities []float64) float64 {
	var e float64 = 0
	for i := range probabilities {
		e += -probabilities[i] * math.Log2(probabilities[i])
	}
	return e
}

func dependEntropy (p [][]float64) float64 {
	var e float64 = 0
	for i := range p {
		for j := range p[i] {
			e += p[i][j]
		}
	}
	return e
}

// предполагается, что массив symbols содежит набор уникальных символов
// чтобы не выполнять поиск по выходным данным
func calcResultProbabilities(data []byte) ([]float64, error) {
	if len(symbols) == 0 || symbols == nil {
		fmt.Print("symbols array is empty!\n")
		readFileWithSymbolsSet()
	}
	symbolsAmounts := make([]int64, len(symbols))
	for i := range data {
		for s := range symbols {
			if symbols[s][0] == data[i] {
				symbolsAmounts[s]++
			}
		}
	}
	probabilities := make([]float64, len(symbols))
	for i := range symbolsAmounts {
		probabilities[i] = float64(symbolsAmounts[i]) / float64(size)
	}
	return probabilities, nil
}

// перед генераций должен быть вызван symbolGenerationInit()
func generateAndWriteSample(resultFile *os.File) {
	fmt.Println("Generating dataset...")
	symbolGenerationInit()
	rand.Seed(time.Now().UnixNano())
	writer := bufio.NewWriter(resultFile)
	var buffer bytes.Buffer
	for i := int64(0); i < size; i++ {
		buffer.WriteString(generateSymbol())
		if i%10000 == 0 {
			_, _ = writer.WriteString(buffer.String())
			_ = writer.Flush()
			buffer.Reset()
			rand.Seed(time.Now().UnixNano())
		}
	}
	_, _ = writer.WriteString(buffer.String())
	_ = writer.Flush()
	buffer.Reset()
	fmt.Println("sample file generated successfully!")
}

//вычисление массива сумм вероятностей для процесса генерации
func symbolGenerationInit() {
	sums = make([]float64, len(nonDependChances))
	var sum float64 = 0
	var buf float64
	for i := range nonDependChances {
		buf, _ = nonDependChances[i].Float64()
		sum += buf
		sums[i] = sum
	}
}

func checkSumProbabilities() error {
	if len(nonDependChances) != len(symbols) {
		return errors.New("amount of symbols != amount of probabilities\n")
	}
	sum := big.NewFloat(0)
	sum.SetPrec(65)
	var buf *big.Float
	for i := range nonDependChances {
		sum.Add(sum, &(nonDependChances)[i])
	}
	buf = big.NewFloat(1)
	buf.SetPrec(65)
	if sum.Cmp(buf) != 0 {
		return errors.New("sum of probabilities != 1\n")
	}
	fmt.Println("Probabilities sum is correct and equal to 1")
	return nil
}

func generateAndWriteDependSample(resultFile *os.File) {
	fmt.Println("Generating dataset...")
	rand.Seed(time.Now().UnixNano())
	writer := bufio.NewWriter(resultFile)
	var buffer bytes.Buffer

	initialProbabilitiesTest()

	prevI, prevS := generateDependSymbol(0)
	for i := int64(1); i < size; i++ {
		buffer.WriteString(prevS)
		prevI, prevS = generateDependSymbol(prevI)
	}

	generatedSuccessfully(&buffer, writer)
}

/*
	для решения слау:
	1) проходимся по главной диагонали матрици, вычитая из каждого эллемента 1 (единицу)
	получая таким образом матрицу a для решения слау
	2) вектор b состоит из нулей, за исключением последнего значения, которое равно 1
*/
func initialProbabilitiesTest () {
	a := make([][]float64, len(dependChances))
	for i := range a {
		a[i] = make([]float64, len(dependChances))
	}
	var f float64
	for i := range a {
		for j := range a[i] {
			f , _ = dependChances[i][j].Float64()
			a[i][j] = f
		}
	}
	for i := range dependChances {
		a[i][i] -= 1
	}
	b := make([]float64, len(dependChances))
	b[len(b)-1] = 1

	x := cholesky(&a,&b)
	fmt.Println("Initial probabilities test:", x)
}

func generateDependSymbol(prev int) (int, string) {
	var (
		randomVal = rand.Float64()
		sum, buf  float64
	)
	sum = 0
	for i := 0; i < len(dependChances[prev]); i++ {
		buf, _ = dependChances[prev][i].Float64()
		sum += buf
		if sum > randomVal {
			return i, symbols[i]
		}
	}
	return 0, symbols[0]
}

func generateSymbol() string {
	var randomVal = rand.Float64()
	for i := 0; i < len(nonDependChances); i++ {
		if sums[i] > randomVal {
			return symbols[i]
		}
	}
	return symbols[0]
}

func checkDependProbabilities() {
	for i := range dependChances {
		if len(dependChances[i]) != len(symbols) {
			fmt.Println("the " + strconv.Itoa(i) + "th string of probabilities matrix is wrong\n")
			panic(1)
		}
	}
	sum := big.NewFloat(0)
	sum.SetPrec(100)
	var buf *big.Float
	buf = big.NewFloat(1)
	buf.SetPrec(100)
	for i := range dependChances {
		for j := range dependChances[i] {
			sum.Add(sum, &(dependChances)[i][j])
		}
		sumFloat, _ := sum.Float64()
		bufFloat, _ := sum.Float64()
		if (sum.Cmp(buf) != 0) && (sumFloat != bufFloat) {
			fmt.Println("sum of probabilities != 1 on string" , i, " with sum = ", sum.String())
			panic(1)
		}
		sum.SetFloat64(0)
	}
	fmt.Println("probabilities sum is correct and equal to 1 on every string")
}
