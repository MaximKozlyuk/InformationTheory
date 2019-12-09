package examples

import (
	"math"
	"testing"
)

type employee struct {
	name string
	age int
	gender bool
}
//
//func TestExample(t *testing.T)) {
//	emp1 := employee{
//		name:   "Bob",
//		age:    23,
//		gender: true,
//	}
//	fmt.Printf("%T", emp1)
//}

func TestAbs(t *testing.T) {
	got := math.Abs(-1)
	if got != 1 {
		t.Errorf("Abs(-1) = %f; want 1", got)
	}
}

//// go binary encoder
//func toGOB64(p phrase) string {
//	b := bytes.Buffer{}
//	e := gob.NewEncoder(&b)
//	err := e.Encode(p)
//	if err != nil { fmt.Println("failed gob Encode", err) }
//	return base64.StdEncoding.EncodeToString(b.Bytes())
//}
//
//// go binary decoder
//func fromGOB64(str string) phrase {
//	m := phrase{}
//	by, err := base64.StdEncoding.DecodeString(str)
//	if err != nil { fmt.Println("failed base64 Decode", err) }
//	b := bytes.Buffer{}
//	b.Write(by)
//	d := gob.NewDecoder(&b)
//	err = d.Decode(&m)
//	if err != nil { fmt.Println("failed gob Decode", err) }
//	return m
//}
