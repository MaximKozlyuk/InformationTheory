package main

import (
	"errors"
	"fmt"
)

func main() {
	fmt.Println("Hello world!")
	f1 := Fraction{
		true, 3, 5,
	}
	f2 := Fraction{
		true, 4, 6,
	}
	f1.add(&f2)
	fmt.Println(f1)
}

// true sign +, false -
type Fraction struct {
	sign bool
	dividend int64		// делимое
	divider int64		// делитель
}

func newFraction (sign bool, dividend, divider int64) *Fraction {
	if divider == 0 {
		return nil
	}
	return &Fraction{sign, dividend, divider}
}

func (frac Fraction) add (x *Fraction) {

}

func (frac Fraction) sub (x *Fraction) {

}

func (frac Fraction) mul (x *Fraction) {

}

func (frac Fraction) div (x *Fraction) {

}

func (frac Fraction) float () float64 {
	if frac.divider == 0 {
		panic(errors.New("dividing by zero"))
	}
	val := float64(frac.dividend / frac.divider)
	if frac.sign {
		return val
	} else {
		return -val
	}
}

