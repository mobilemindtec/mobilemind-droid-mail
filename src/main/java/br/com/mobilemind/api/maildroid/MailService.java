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
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import br.com.mobilemind.api.droidutil.dialog.Dialog;
import br.com.mobilemind.api.droidutil.dialog.DialogResult;
import br.com.mobilemind.api.droidutil.dialog.OnRespostEvent;
import br.com.mobilemind.api.droidutil.dialog.RespostaListener;
import br.com.mobilemind.api.droidutil.logs.AppLogger;
import br.com.mobilemind.api.droidutil.logs.LoggerConfigurarionBuilder;
import br.com.mobilemind.api.droidutil.rest.WsExecutor;
import br.com.mobilemind.api.droidutil.tools.DeviceInfo;
import br.com.mobilemind.api.droidutil.tools.ProgressBarManager;
import br.com.mobilemind.api.json.JSON;
import br.com.mobilemind.api.maildroid.event.MailDroidProcessListener;
import br.com.mobilemind.api.maildroid.extra.MailDroidResource;
import br.com.mobilemind.api.security.key.Base64;
import br.com.mobilemind.api.utils.DateUtil;
import br.com.mobilemind.api.utils.MobileMindUtil;
import br.com.mobilemind.api.utils.log.MMLogger;
import br.com.mobilemind.api.droidutil.ioc.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Ricardo Bocchi
 */
public class MailService {

    @Inject
    private Context context;
    private WsExecutor executor;
    private ProgressBarManager progressBar;
    private List<MailDroidProcessListener> mailDroidProcessListener = new LinkedList<MailDroidProcessListener>();
    private boolean backgroundMode;

    public MailService() {
    }

    public MailService(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            progressBar = new ProgressBarManager((Activity) context, "Carregando.. Aguarde...");
        }
        executor = new WsExecutor(context);
    }

    public void setCheckConnection(boolean val) {
        executor.setTestConnection(val);
    }

    public void defineProgressBarManager(View view) {
        progressBar = new ProgressBarManager(context, view, -1);
    }

    public void setMailDroidProcessListener(MailDroidProcessListener mailDroidProcessListener) {
        if (mailDroidProcessListener != null) {
            this.mailDroidProcessListener.add(mailDroidProcessListener);
        }
    }

    public void setBackgroundMode(boolean backgroundMode){
        this.backgroundMode = backgroundMode;        
    }

    /**
     * send a new e-mail
     *
     * @param email
     * @throws MessagingException
     */
    public void send(final Email email) {

        if(!this.backgroundMode){
            if (progressBar != null) {
                progressBar.setMessage("Enviando e-mail.. Aguarde..");
                progressBar.setTitle("Mobile Mind");
                progressBar.openProgressDialog();
            }
        }

        if (executor == null) {
            executor = new WsExecutor();
        }

        if(!this.backgroundMode){
            executor.setTestConnection(true);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (!MobileMindUtil.isNullOrEmpty(email.getAnexoFile())) {
                        File anexo = new File(email.getAnexoFile());
                        FileInputStream fis = new FileInputStream(anexo);
                        //System.out.println(file.exists() + "!!");
                        //InputStream in = resource.openStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        try {
                            byte[] buf = new byte[1024];

                            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                                bos.write(buf, 0, readNum); //no doubt here is 0
                            }

                            String content = Base64.encodeBytes(bos.toByteArray(), Base64.GZIP);
                            email.setAnexo(content);
                            email.setAnexoName(anexo.getName());
                        } finally {
                            fis.close();
                            bos.close();
                        }
                    }

                    final JSON<Email> jsonConverter = new JSON<Email>(Email.class);
                    executor.setBaseUrl(MailDroidResource.getProperty("br.com.mobilemind.mailserver"));
                    executor.setEntity(jsonConverter.toJSON(email).toString());
                    executor.executePost();

                    if(!backgroundMode){
                        if (progressBar != null) {
                            progressBar.closeProgressDialog();
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    Dialog.showInfo(context, "O e-mail foi enviado com sucesso!",  new RespostaListener() {
                                    @Override
                                    public void onCancel() {
                                        for (MailDroidProcessListener it : mailDroidProcessListener) {
                                            it.onSuccess();
                                        }                                        
                                    }

                                    @Override
                                    public void onOk() {
                                        for (MailDroidProcessListener it : mailDroidProcessListener) {
                                            it.onSuccess();
                                        }
                                    }
                                });
                                }
                            });
                        }
                    }
                } catch (final Exception e) {
                    if(!backgroundMode){
                        if (progressBar != null) {
                            progressBar.closeProgressDialog();
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    Dialog.showInfo(context, MailDroidResource.getProperty("br.com.mobilemind.report.error"),  new RespostaListener() {
                                    @Override
                                    public void onCancel() {
                                        for (MailDroidProcessListener it : mailDroidProcessListener) {
                                            it.onError(e);
                                        }                                        
                                    }

                                    @Override
                                    public void onOk() {
                                        for (MailDroidProcessListener it : mailDroidProcessListener) {
                                            it.onError(e);
                                        }
                                    }
                                });
                                }
                            });
                        }
                    }

                    MMLogger.log(Level.SEVERE, MailService.class, e);
                    
                }
            }
        }).start();
    }

    public void sendLogs() {
        this.sendLogs(null);
    }

    /**
     * send application logs to Mobile Mind
     *
     * @throws MessagingException
     */
    public void sendLogs(String body) {

        Email email = create(true);

        String packageName = context.getApplicationInfo().packageName;
        int versionNumber = 0;
        String versionName = "nada";

        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, PackageManager.GET_META_DATA);
            versionNumber = pi.versionCode;
            versionName = pi.versionName;
        } catch (Exception e) {
            AppLogger.error(getClass(), e);
        }

        email.setSubject("Mobile Mind Logs - " + packageName + ": " + versionNumber + " - " + versionName);

        if (body == null) {
            StringBuilder body2 = new StringBuilder();
            body2.append("<br/><br/>").append("DateTime: ").append(DateUtil.timestampToStr(new Date()));
            body2.append("<br/><br/>");
            body2.append(DeviceInfo.getDeviceInfo(context));

            body2.append("<br/><br/>Não responda esse e-mail, ele foi gerado pelo sistema da Mobile Mind.");
            body2.append("<br/> Suporte Mobile Mind");
            body = body2.toString();
        }

        email.setBody(body);
        email.setAnexoFile(LoggerConfigurarionBuilder.FILE_LOG);

        final MailDroidProcessListener listener = new MailDroidProcessListener() {
            @Override
            public void onSuccess() {
                try {
                    File file = new File(LoggerConfigurarionBuilder.FILE_LOG);
                    FileWriter writer = new FileWriter(file, false);
                    writer.write("");
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    MMLogger.log(Level.SEVERE, this.getClass(), e.getMessage(), e);
                }
                mailDroidProcessListener.remove(this);
            }

            @Override
            public void onError(Exception e) {
                mailDroidProcessListener.remove(this);
            }

            @Override
            public void onCancel() {
                mailDroidProcessListener.remove(this);
            }

        };

        this.mailDroidProcessListener.add(listener);
        send(email);
    }

    public void sendDatabaseBackup(File databaseFile) {

        Email email = create(true);

        StringBuilder body = new StringBuilder("Mobile Mind Data Base Backup - ").append(context.getApplicationInfo().packageName);

        email.setSubject(body.toString());

        body.append("<br/><br/>").append("DateTime: ").append(DateUtil.timestampToStr(new Date()));
        body.append("<br/><br/>");
        body.append(DeviceInfo.getDeviceInfo(context));

        body.append("<br/><br/>Não responda esse e-mail, ele foi gerado pelo sistema da Mobile Mind.");
        body.append("<br/> Suporte Mobile Mind");
        email.setBody(body.toString());
        email.setAnexoFile(databaseFile.getAbsolutePath());
        send(email);
    }

    /**
     * create default e-mail with mail_conf.properties informations
     *
     * @param addDefaultTo set default to
     * @return
     */
    public Email create(boolean addDefaultTo) {
        Email email = new Email();
        email.setApplication(context.getApplicationInfo().packageName);
        email.setFrom(MailDroidResource.getProperty("br.com.mobilemind.from"));
        email.setPassword(MailDroidResource.getProperty("br.com.mobilemind.from.password"));
        if (addDefaultTo) {
            email.addTo(MailDroidResource.getProperty("br.com.mobilemind.log.listener"));
        }
        return email;
    }

    public void showReportDialog() {
        showReportDialog(MailDroidResource.getProperty("br.com.mobilemind.report.question"));
    }

    public void showReportDialog(Exception e) {
        String message = MessageFormat.format(MailDroidResource.getProperty("br.com.mobilemind.report.question.descricao"), e.getMessage());
        showReportDialog(message);
    }

    public void showReportDialog(String message) {
        Dialog.showReportQuestion(context, message, new OnRespostEvent() {
            @Override
            public void responded(DialogResult respost) {
                switch (respost) {
                    case YES:
                        sendLogs();
                        break;
                    case NO:
                        for (MailDroidProcessListener it : mailDroidProcessListener) {
                            it.onCancel();
                        }
                        break;
                }
            }
        });
    }
}
