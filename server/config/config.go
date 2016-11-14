package config

import (
	"os"
	"log"
	"encoding/json"
	"fmt"
)

type config struct {
	MongoServerIP string
	MongoDatabaseName string
	ItemImagesPath string
	ItemThumbnailImagesPath string
	StaticUrl string
	ThumbnailUrl string
	FullImageUrl string
}

var Config config

func init()  {
	pwd, _ := os.Getwd()
	fmt.Println(pwd)
	file, err  := os.Open(pwd +"/config/config.json")
	if(err != nil){
		log.Fatal("Couldn't find  config.json file")
	}
	decoder := json.NewDecoder(file)
	err = decoder.Decode(&Config)
	if(err != nil){
		log.Fatal("Could't parse config file")
	}
	log.Printf("Config : \n %v",Config)
}

