package org.mengyun.tcctransaction.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/30/15.
 */
public class TransactionContext implements Serializable {

    private static final long serialVersionUID = -8199390103169700387L;
    private TransactionXid xid;//id

    private int status;//状态

    private Map<String, String> attachments = new ConcurrentHashMap<String, String>();//附件信息

    public TransactionContext() {

    }

    public TransactionContext(TransactionXid xid, int status) {
        this.xid = xid;
        this.status = status;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    public TransactionXid getXid() {
        return xid.clone();
    }

    public void setAttachments(Map<String, String> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            this.attachments.putAll(attachments);
        }
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


}
