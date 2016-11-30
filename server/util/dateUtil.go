package util

import "time"

func GetCurrentMilis() int64{
	return time.Now().UnixNano() / (int64(time.Millisecond) / int64(time.Nanosecond) )
}
