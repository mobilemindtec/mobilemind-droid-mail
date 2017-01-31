package br.com.mobilemind.api.maildroid;

/*
 * #%L
 * Mobile Mind - Mail Droid
 * %%
 * Copyright (C) 2012 Mobile Mind Empresa de Tecnologia
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
import br.com.mobilemind.api.json.annotations.JsonColumn;
import br.com.mobilemind.api.json.annotations.JsonEntity;
import br.com.mobilemind.api.utils.MobileMindUtil;

/**
 *
 * @author Ricardo Bocchi
 */
@JsonEntity
public class Email {

    @JsonColumn
    private String subject;
    @JsonColumn
    private String body;
    @JsonColumn
    private String from;
    @JsonColumn
    private String fromName;
    @JsonColumn
    private String printable;
    private String password;
    @JsonColumn
    private String to = "";
    @JsonColumn
    private String anexo;
    private String anexoFile;
    @JsonColumn
    private String application;
    @JsonColumn
    private String anexoName;
    @JsonColumn
    private String gmailUserName;
    @JsonColumn
    private String gmailPassword;
    
    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public Email addTo(String to) {
        if (!MobileMindUtil.isNullOrEmpty(this.to)) {
            this.to += ",";
        }
        this.to += to;
        return this;
    }

    public String getTo() {
        return to;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnexoFile() {
        return anexoFile;
    }

    public void setAnexoFile(String anexoFile) {
        this.anexoFile = anexoFile;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getAnexoName() {
        return anexoName;
    }

    public void setAnexoName(String anexoName) {
        this.anexoName = anexoName;
    }

    public String getPrintable() {
        return printable;
    }

    public void setPrintable(String printable) {
        this.printable = printable;
    }

    public String getGmailUserName() {
        return gmailUserName;
    }

    public void setGmailUserName(String gmailUserName) {
        this.gmailUserName = gmailUserName;
    }

    public String getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(String gmailPassword) {
        this.gmailPassword = gmailPassword;
    }
}
