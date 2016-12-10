package util

import (
	"time"
	"math/rand"
)

func Random(min, max int) int {
	rand.Seed(time.Now().Unix())
	return rand.Intn(max - min) + min
}
