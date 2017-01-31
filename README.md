# mobilemind-droid-mail
Android mail sender

### configure 
```
// create src/main/resources/mail_conf.properties
br.com.mobilemind.log.listener=name@email.com.br - default user to receive erros logs
br.com.mobilemind.from=name@gmail.com - send message using this email
br.com.mobilemind.from.password=password - send message using this password
br.com.mobilemind.report.question=Ocorreu um erro na aplicação. Você deseja enviar o relatório de erros para o desenvolvedor nesse momento?
br.com.mobilemind.report.question.descricao=Ocorreu um erro na aplicação. Detalhes: {0}. Você deseja enviar o relatório de erros para o desenvolvedor nesse momento?
br.com.mobilemind.mailserver=utr to post json

```
### use
```

MailService mailService = new MailService(context);

RespostaListener respostaListener = new RespostaListener() {

    @Override
    public void onCancel() {
    
    }

    @Override
    public void onOk() {
    }
};

// open error report dialog
mailService.showReportDialog(error);

// send database backup 
mailService.sendDatabaseBackup(new File("/backup/location.bkp"));

// send simple email
Email email = new Email();
email.setSubject("subject");
email.setBody("html body");
email.setFromName("myAppName")
mailService.send(email);




```
