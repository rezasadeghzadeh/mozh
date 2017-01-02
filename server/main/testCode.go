package main

import (
	"fmt"
	//"golang.org/x/net/ipv4"
	"net"
)

type Node struct {
	Id string
	Name string
}

func main() {
	//b:= []byte{69,0, 0, 56, 160, 147, 0, 0, 254, 1, 56, 118, 192, 168, 177, 1, 192, 168, 177, 104, 11, 0, 244, 255, 0, 0, 0, 0, 69, 0, 0, 52, 81, 134, 0, 0, 0, 1, 241, 44, 192, 168, 177, 104, 4, 2, 2 ,4, 8, 0, 157, 115, 90 ,139, 0 ,1}
	//fmt.Printf("%v",b[28:48])
	//h, err := ipv4.ParseHeader(b[28:48])
	//if err != nil{
	//	fmt.Printf("%v",err)
	//}

	//fmt.Printf("\n%#v",h.ID);

	ips,err := net.LookupIP("www.google.com")
	if err != nil{

	}
	for _,ip := range  ips{
		fmt.Printf("%s\n",ip.String())
	}

}
