package util

import (
	"strings"
)

func CommaSeperateToSlice(commaSeperated string) []string{
	var result  []string
	if len(strings.TrimSpace(commaSeperated)) == 0{
		return result
	}

	for _,str := range  strings.Split(commaSeperated,","){
		result  = append(result,str)
	}
	return result
}
