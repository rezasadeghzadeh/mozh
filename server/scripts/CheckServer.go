package main

import (
	"github.com/labstack/gommon/log"
	"net/http"
	"gopkg.in/square/go-jose.v1/json"
	"io"
	"bytes"
	"../util"
	"fmt"
	"os/exec"
)
const(
	url = "http://209.59.209.181:7777/category/list"
	emailTo = "reza.sadeqzadeh@gmail.com"
	subject  = "Mozhdeh Server Problem"
)

type Category struct {
	Id string
	Title string
}

func main() {
	log.Printf("Start Mozhdeh Server Checking")
	categories := []Category{}

	res, err := http.Get(url)
	if err != nil{
		log.Printf("Error : %v",err)
		restartServer()
		sendEmail()
		return
	}
	defer res.Body.Close()
	var b bytes.Buffer
	_, err =  io.Copy(&b, res.Body)
	if err != nil{
		log.Printf("Error %v",err)
	}
	json.Unmarshal(b.Bytes(),&categories)
	log.Printf("Categories List: %v",categories)
	if len(categories) == 0{
		restartServer()
		sendEmail()
	}
}

func sendEmail() {
	body :=  fmt.Sprintf("The %s is not return correct values, try to restart server or check more for the problem",url)
	log.Printf("Sending Email to administrator")
	util.SendEmail(emailTo, subject, body)
}

func restartServer() {
	log.Printf("Restarting Mozhdeh Server")
	killProcess  :=  "/usr/bin/killall main"
	cmd  := exec.Command(killProcess)
	err := cmd.Run()
	if err != nil{
		log.Printf("Error on killing process %v",err)
	}
	startServer :=  "/opt/mozhdeh/server/start.sh"
	cmd = exec.Command(startServer)
	err = cmd.Run()
	if err != nil{
		log.Printf("Error on Startting server %v",err)
	}
}

