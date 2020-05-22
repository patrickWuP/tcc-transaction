package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionXid;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by changmingxie on 11/12/15.  事物的存储层
 */
public interface TransactionRepository {

    int create(Transaction transaction);//创建事物节点

    int update(Transaction transaction);//更新事物节点

    int delete(Transaction transaction);//删除事物节点

    Transaction findByXid(TransactionXid xid);//根据xid获取事物

    List<Transaction> findAllUnmodifiedSince(Date date);//根据日期获取所有未修改的事物
}
