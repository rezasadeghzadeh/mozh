package main

import (
	"fmt"
	//"golang.org/x/net/ipv4"
	"golang.org/x/net/ipv4"
	"math/rand"
	"github.com/labstack/gommon/log"
)

type Node struct {
	Id string
	Name string
}

type resultMap map[string][30]string


func main() {
	b:= []byte{69,0, 0, 56, 160, 147, 0, 0, 254, 1, 56, 118, 192, 168, 177, 1, 192, 168, 177, 104, 11, 0, 244, 255, 0, 0, 0, 0, 69, 0, 0, 52, 81, 134, 0, 0, 0, 1, 241, 44, 192, 168, 177, 104, 4, 2, 2 ,4, 8, 0, 157, 115, 90 ,139, 0 ,1}
	fmt.Printf("%v",b[28:48])
	h, err := ipv4.ParseHeader(b[28:48])
	if err != nil{
		fmt.Printf("%v",err)
	}

	fmt.Printf("\n%#v \n",h.ID);
	fmt.Printf("%d \n",int(b[54]))
	fmt.Printf("%d \n",int(b[54])<<8)
	fmt.Printf("%d \n",int(b[55]))
	fmt.Printf("%d \n",int(b[52])<<8 | int(b[53]))

	for i:=0;i<=9;i++{
		lport := 30000 + randInt(1,20000)
		log.Printf("%v",lport)
	}

	result  := resultMap{}
	result["4.2.2.4"] = [30]string{}
	fmt.Printf("%v",result)
}

func randInt(min int, max int) int {
	return min + rand.Intn(max-min)
}
