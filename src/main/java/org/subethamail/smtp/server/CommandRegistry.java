/*
 * Commands.java Created on November 18, 2006, 12:26 PM To change this template,
 * choose Tools | Template Manager and open the template in the editor.
 */

package org.subethamail.smtp.server;

import org.subethamail.smtp.command.*;

/**
 * Enumerates all the Commands made available in this release.
 *
 * @author Marco Trevisan <mrctrevisan@yahoo.it>
 */
public enum CommandRegistry
{
	AUTH(new AuthCommand(), true, false),
	DATA(new DataCommand(), true, true),
	EHLO(new EhloCommand(), false, false),
	HELO(new HelloCommand(), true, false),
	HELP(new HelpCommand(), true, true),
	MAIL(new MailCommand(), true, true),
	NOOP(new NoopCommand(), false, false),
	QUIT(new QuitCommand(), false, false),
	RCPT(new ReceiptCommand(), true, true),
	RSET(new ResetCommand(), true, false),
	STARTTLS(new StartTLSCommand(), false, false),
	VRFY(new VerifyCommand(), true, true),
	EXPN(new ExpandCommand(), true, true),
	XCLIENT(new XclientCommand(), true, true);

	private Command command;

	/** */
	private CommandRegistry(Command cmd, boolean checkForStartedTLSWhenRequired, boolean checkForAuthIfRequired)
	{
		if (checkForStartedTLSWhenRequired)
			this.command = new RequireTLSCommandWrapper(cmd);
		else
			this.command = cmd;
        if (checkForAuthIfRequired)
            this.command = new RequireAuthCommandWrapper(this.command);
	}

	/** */
	public Command getCommand()
	{
		return this.command;
	}
}
