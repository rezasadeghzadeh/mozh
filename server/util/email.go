package util

import (
	"github.com/zemirco/email"
	"../config"
	"github.com/labstack/gommon/log"
)

func SendEmail(to string, subject string, body string) error{
	mail := email.Mail{
		From:  config.Config.SmtpEmail  ,
		To:      to,
		Subject: subject,
		HTML:    body,
	}
	log.Printf("Add: %s",config.Config.SmtpPassword)
	err := mail.Send(config.Config.SmtpAdd, config.Config.SmtpPort, config.Config.SmtpEmail, config.Config.SmtpPassword)
	if err != nil {
		return err
	}
	return nil
}
