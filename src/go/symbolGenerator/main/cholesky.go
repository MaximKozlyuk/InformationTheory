package main

import (
	"fmt"
	"math"
)

func cholesky (a *[][]float64, b *[]float64) []float64 {
	*b = vectorMulMatrix(matrixTranspose(a),b)
	L := decomposition(matrixMul(matrixTranspose(a),a))
	Lt := matrixTranspose(L)

	var sum float64
	y := make([]float64, len(*a))
	x := make([]float64, len(*a))
	for i := 0; i < len(*a); i++ {
		sum = 0
		for j := 0; j < i; j++ {
			sum += (*L)[i][j] * y[j]
		}
		y[i] = ((*b)[i] - sum) / (*L)[i][i]
	}
	// len(*a)-1
	fmt.Println("Lt's")
	for i := len(*b)-1; i > -1; i-- {
		sum = 0
		for j := len(*a) - 1; j > i; j-- {
			sum += (*Lt)[i][j] * x[j]
		}
		fmt.Println((*Lt)[i][i])
		x[i] = (y[i] - sum) / (*Lt)[i][i]
	}
	return x
}

func vectorMulMatrix (a *[][]float64, b *[]float64) []float64 {
	result := make([]float64, len(*a))
	for i := 0; i < len(*a); i++ {
		for j := 0; j< len(*a); j++ {
			result[i] += (*a)[i][j] * (*b)[j]
		}
	}
	return result
}

func matrixTranspose (a *[][]float64) *[][]float64 {
	result := make([][]float64, len(*a))
	for i := range result {
		result[i] = make([]float64, len(*a))
	}
	for i := 0; i < len((*a)[0]); i++ {
		for j := 0; j < len((*a)[0]); j++ {
			result[i][j] = (*a)[j][i]
		}
	}
	return &result
}

func matrixMul (a,b *[][]float64) *[][]float64 {
	resultMatrix := make([][]float64, len(*a))
	for i := range resultMatrix {
		resultMatrix[i] = make([]float64, len(*a))
	}
	for i := 0; i < len(*a); i++ {
		for j := 0; j < len(*a); j++ {
			for k := 0; k < len(*a); k++ {
				resultMatrix[i][j] += (*a)[i][k] * (*b)[k][j]
			}
		}
	}
	return &resultMatrix
}

func decomposition (a *[][]float64) *[][]float64 {
	L := make([][]float64, len(*a))
	for i := range L {
		L[i] = make([]float64, len(*a))
	}
	var sumJ, sumI float64
	L[0][0] = math.Sqrt((*a)[0][0])
	for i := 1; i < len(*a); i++ {
		sumJ =0
		for j := 1; j < len(*a); j++ {
			if j - 1 < i {
				for k := 0; k < j-1; k++ {
					sumJ += L[i][k] * L[j-1][k]
				}
				L[i][j-1] = ((*a)[i][j-1] - sumJ) / L[j-1][j-1]
			}
		}
		sumI = 0
		for k := 0; k < i; k++ {
			sumI += L[i][k] * L[i][k]
		}
		L[i][i] = math.Sqrt((*a)[i][i] - sumI)
	}
	return &L
}

