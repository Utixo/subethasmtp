package org.subethamail.smtp.command;

import org.subethamail.smtp.DropConnectionException;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.Session;
import org.subethamail.smtp.util.Base64;
import org.subethamail.smtp.util.InternetAddressUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by seand on 2017-07-12.
 */
public class XclientCommand extends BaseCommand {

    public XclientCommand() {
        super("XCLIENT",
                "Override one or more client-related session attributes.",
                "[NAME=<name>] [ADDR=<client ip address>] [PORT=<client port>] [LOGIN=<user alias>:<base 64 encoded password>] [DESTADDR=<target smtp ip address>] [DESTPORT=<target smtp port>]");
    }

    @Override
    public void execute(String commandString, Session sess) throws IOException, DropConnectionException {
        if (sess.isMailTransactionInProgress()) {
            sess.sendResponse("503 Mail transaction in progress");
            return;
        } else if (commandString.trim().equals("XCLIENT")) {
            sess.sendResponse("501 Missing parameters");
        }

        StringTokenizer tokens = new StringTokenizer(commandString);
        // Skip the XCLIENT part
        tokens.nextToken();

        while(tokens.hasMoreTokens()) {
            String parameter = tokens.nextToken();
            List<String> parameterValuePair = this.extractParameterNameValuePair(parameter, "=");
            if (parameterValuePair == null || parameterValuePair.size() != 2) {
                sess.sendResponse("501 Parameter format error, must be PARAM=VALUE");
                return;
            }
            String parameterName = parameterValuePair.get(0).toUpperCase(Locale.ENGLISH);
            String parameterValue = parameterValuePair.get(1);
            if (parameterValue.isEmpty()) {
                sess.sendResponse("501 Parameter format error, must be PARAM=VALUE");
                return;
            }
            boolean paramaterTypeUnavailable = isUnavailable(parameterValue);
            // Decode the parameter name and set the parameter value correctly
            if (parameterName.equals("NAME")) {
                if (!paramaterTypeUnavailable) { 
                    sess.setRemoteClientName(parameterName);
                } else {
                    sess.setRemoteClientName(null);
                }
            } else if (parameterName.equals("ADDR")) {
                if (!paramaterTypeUnavailable) {
                    if (!InternetAddressUtils.isValidSingleAddress(parameterValue)) {
                        sess.sendResponse("501 address is not a valid internet address");
                        return;
                    }
                    sess.setRemoteClientIP(parameterValue);
                } else {
                    sess.setRemoteClientIP(null);
                }
            } else if (parameterName.equals("PORT")) {
                if (!paramaterTypeUnavailable) { 
                    sess.setRemoteClientPort(Integer.parseInt(parameterValue, 10));
                } else {
                    sess.setRemoteClientPort(null);
                }
            } else if (parameterName.equals("PROTO")) {
                // TODO add support for this
            } else if (parameterName.equals("HELO")) {
                if (!paramaterTypeUnavailable) { 
                    sess.setHelo(parameterValue);
                }
            } else if (parameterName.equals("LOGIN")) {
                String userName = null;
                String password = null;
                if (!paramaterTypeUnavailable) { 
                    List<String> userPasswordPair = this.extractParameterNameValuePair(parameterValue, ":");
                    if (userPasswordPair != null && userPasswordPair.size() > 0 && userPasswordPair.size() <= 2) {
                        userName = userPasswordPair.get(0);
                        if (isUnavailable(userName)) {
                            userName = null;
                        }
                        if (userPasswordPair.size() > 1) {
                            password = userPasswordPair.get(1);
                            if (isUnavailable(password)) {
                                password = null;
                            } else {
                                byte[] decoded = Base64.decode(password);
                                if (decoded == null) {
                                    sess.sendResponse("501 Invalid command argument, not a valid Base64 string");
                                    return;
                                }
                                password = new String(decoded);
                            }
                        }
                    } else {
                        sess.sendResponse("501 Parameter format error, must be LOGIN=<user alias>:<base 64 encoded password>");
                        return;
                    }
                }
                sess.setUsername(userName);
                sess.setPassword(password);
            } else if (parameterName.equals("DESTADDR")) {
                if (!paramaterTypeUnavailable) {
                    if (!InternetAddressUtils.isValidSingleAddress(parameterValue)) {
                        sess.sendResponse("501 address is not a valid internet address");
                        return;
                    }
                    sess.setDestinationIP(parameterValue);
                } else {
                    sess.setDestinationIP(null);
                }
            } else if (parameterName.equals("DESTPORT")) {
                if (!paramaterTypeUnavailable) { 
                    sess.setDestinationPort(Integer.parseInt(parameterValue, 10));
                } else {
                    sess.setDestinationPort(null);
                }
            } else {
                sess.sendResponse(String.format("501 Parameter %s not supported", parameterName));
                return;
            }
        }

        sess.sendResponse("220 ok");
    }

    private List<String> extractParameterNameValuePair(String token, String delimiter) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        List<String> pairList = new ArrayList<String>();
        int delimiterPosition = token.indexOf(delimiter);
        if (delimiterPosition >= 0) {
            // Extract the first part on the left side
            if (delimiterPosition > 0) {
                pairList.add(token.substring(0, delimiterPosition).trim());
            } else {
                pairList.add("");
            }
            // extract the right side
            if (delimiterPosition < (token.length() - 1)) {
                pairList.add(token.substring(delimiterPosition + 1).trim());
            } else {
                pairList.add("");
            }

            return pairList;
        } else {
            pairList.add(token.trim());
        }

        return pairList;
    }

    private boolean isUnavailable(String value) {
        return value == null || value.isEmpty() || value.equalsIgnoreCase("[UNAVAILABLE]") ||
                value.equalsIgnoreCase("[TEMPUNAVAIL]");
    }
}
