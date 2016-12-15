package main

import (
	"fmt"
	"time"
)

type Node struct {
	Id string
	Name string
}

func main()  {
	for i:=0;i<=10;i++{
		fmt.Printf("%d\t",i)
		go func(index int) {
			fmt.Printf("%d\n",index)
		}(i)
	}
	time.Sleep(time.Second*1)
}
