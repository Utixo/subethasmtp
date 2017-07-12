package org.subethamail.smtp.command;

import org.subethamail.smtp.DropConnectionException;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.Session;

import java.io.IOException;
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
        if (sess.isMailTransactionInProgress())
        {
            sess.sendResponse("503 5.5.1 Sender already specified.");
            return;
        } else if (commandString.trim().equals("XCLIENT")) {
            sess.sendResponse("501 Missing parameters.");
        }

        StringTokenizer tokens = new StringTokenizer(commandString);
        // Skip the XCLIENT part
        tokens.nextToken();

        while(tokens.hasMoreTokens()) {
            String parameter = tokens.nextToken();
            StringTokenizer parameterTokenizer = new StringTokenizer(parameter, "=");
            if (parameterTokenizer.countTokens() != 2) {
                sess.sendResponse("501 Parameter format error, must be PARAM=VALUE.");
                return;
            }
            String parameterName = parameterTokenizer.nextToken().toUpperCase(Locale.ENGLISH);
            String parameterValue = parameterTokenizer.nextToken();
            if (parameterName.equals("NAME")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("ADDR")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("PORT")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("PROTO")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("HELO")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("LOGIN")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("DESTADDR")) {
                // TODO ignore or add this to he session as this is not used
            } else if (parameterName.equals("DESTPORT")) {
                // TODO ignore or add this to he session as this is not used
            } else {
                sess.sendResponse(String.format("501 Parameter %s not supported.", parameterName));
                return;
            }
        }
    }
}
