package org.subethamail.smtp.command;

import org.subethamail.smtp.util.Base64;
import org.subethamail.smtp.util.EmailUtils;
import org.subethamail.smtp.util.ServerTestCase;

/**
 * Created by seand on 2017-07-13.
 */
public class XclientTest extends ServerTestCase {
    public XclientTest(String name) {
        super(name);
    }

    /** */
    public void testMissingParameters() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT");

        this.expect("501 Missing parameters");
    }

    /** */
    public void testTransactionStarted() throws Exception
    {
        this.wiser.getServer().setMaxMessageSize(1000);
        this.expect("220");

        this.send("EHLO foo.com");
        this.expectContains("250-SIZE 1000");

        this.send("MAIL FROM:<validuser@subethamail.org>");
        this.expect("250 Ok");

        this.send("XCLIENT NAME=spike.porcupine.org ADDR=168.100.189.2");
        this.expect("503 Mail transaction in progress");
    }

    /** */
    public void testMissingParameterValue() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT NAME");
        this.expect("501 Parameter format error, must be PARAM=VALUE");

        this.send("XCLIENT NAME=");
        this.expect("501 Parameter format error, must be PARAM=VALUE");

        this.send("XCLIENT NAME=   ");
        this.expect("501 Parameter format error, must be PARAM=VALUE");
    }

    /** */
    public void testUnsupportedParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT UNKNOWN=value");
        this.expect("501 Parameter UNKNOWN not supported");
    }

    /** */
    public void testNAMEParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT NAME=new.test.com");
        this.expect("220 ok");

        this.send("XCLIENT name=new.test.com");
        this.expect("220 ok");
    }

    /** */
    public void testADDRParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT ADDR=127.0.0.1");
        this.expect("220 ok");

        this.send("XCLIENT addr=127.0.0.1");
        this.expect("220 ok");
    }

    /** */
    public void testADDRBadParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT ADDR=_)()(((");
        this.expect("501 address is not a valid internet address");
    }

    /** */
    public void testPORTParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT PORT=12");
        this.expect("220 ok");

        this.send("XCLIENT port=12");
        this.expect("220 ok");

        this.send("XCLIENT PORT=dd");
        this.expect("421 4.3.0 Mail system failure, closing transmission channel");
    }

    /** */
    public void testDESTADDRParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT DESTADDR=127.0.0.1");
        this.expect("220 ok");

        this.send("XCLIENT destaddr=www.test.org");
        this.expect("220 ok");
    }

    /** */
    public void testDESTADDRBadParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT DESTADDR=_)()(((");
        this.expect("501 address is not a valid internet address");
    }

    /** */
    public void testDESTPORTParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT DESTPORT=12");
        this.expect("220 ok");

        this.send("XCLIENT destport=12");
        this.expect("220 ok");

        this.send("XCLIENT DESTPORT=dd");
        this.expect("421 4.3.0 Mail system failure, closing transmission channel");
    }

    /** */
    public void testHELOParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT HELO=local.test.org");
        this.expect("220 ok");

        this.send("XCLIENT helo=local.test.org");
        this.expect("220 ok");
    }

    /** */
    public void testLOGINUserNameOnlyParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT LOGIN=test@test.org");
        this.expect("220 ok");
    }

    /** */
    public void testLOGINPasswordOnlyParameter() throws Exception
    {
        this.expect("220");

        this.send(String.format("XCLIENT LOGIN=:%s",
                Base64.encodeToString("password".getBytes(), false)));
        this.expect("220 ok");

    }

    /** */
    public void testLOGINUserNameAndPasswordParameter() throws Exception
    {
        this.expect("220");

        this.send(String.format("XCLIENT LOGIN=test@test.org:%s",
                Base64.encodeToString("password".getBytes(), false)));
        this.expect("220 ok");
    }

    /** */
    public void testLOGINNoUserNameOrPasswordParameter() throws Exception
    {
        this.expect("220");

        this.send("XCLIENT LOGIN=:");
        this.expect("220 ok");
    }
}
