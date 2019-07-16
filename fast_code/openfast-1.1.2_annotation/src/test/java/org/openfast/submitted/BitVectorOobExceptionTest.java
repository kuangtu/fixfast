package org.openfast.submitted;

import java.io.ByteArrayOutputStream;
import java.util.List;

import junit.framework.TestCase;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.MessageOutputStream;
import org.openfast.SequenceValue;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class BitVectorOobExceptionTest extends TestCase {

    public void testIt() {
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.load(BitVectorOobExceptionTest.class.getResourceAsStream("/submitted/BitVectorOobExceptionTest.xml"));
        MessageTemplate messageTemplate = loader.getTemplateRegistry().get("MarketDataIncrementalRefresh_25");
        Message outObj = new Message(messageTemplate);
        MessageOutputStream messageOut = new MessageOutputStream(new ByteArrayOutputStream());
        messageOut.registerTemplate(25, messageTemplate);
            outObj.setInteger("MsgSeqNum", 1);
            outObj.setLong("SendingTime", 2);
            outObj.setInteger("TradeDate", 1);
            Sequence MDEntriesSequence = messageTemplate.getSequence("MDEntries");
            SequenceValue MDEntriesSequenceValue = new SequenceValue(MDEntriesSequence);

                GroupValue MDEntriesOutGroup = new GroupValue(MDEntriesSequence.getGroup());

                    MDEntriesOutGroup.setString("MDUpdateAction", "a");
                    MDEntriesOutGroup.setString("MDEntryType", "a");
                    MDEntriesOutGroup.setInteger("RptSeq", 1);
                    MDEntriesOutGroup.setInteger("PriceBandType", 1);
                    MDEntriesOutGroup.setLong("SecurityID", 5);
                    MDEntriesOutGroup.setString("SecurityIDSource", "b");
                    MDEntriesOutGroup.setString("SecurityExchange", "c");
                    MDEntriesOutGroup.setString("MDStreamID", "d");
                    MDEntriesOutGroup.setDecimal("MDEntryPx", 1);
                    MDEntriesOutGroup.setDecimal("MDEntrySize", 2);
                    MDEntriesOutGroup.setInteger("MDEntryDate", 3);
                    MDEntriesOutGroup.setInteger("MDEntryTime", 4);
                    MDEntriesOutGroup.setString("TickDirection", "e");
                    MDEntriesOutGroup.setString("QuoteCondition", "f");
                    MDEntriesOutGroup.setString("TradeCondition", "g");
                    MDEntriesOutGroup.setString("OpenCloseSettlFlag", "h");
                    MDEntriesOutGroup.setLong("NoSharesIssued", 5);
                    MDEntriesOutGroup.setString("Currency", "j");
                    MDEntriesOutGroup.setString("OrderID", "k");
                    MDEntriesOutGroup.setString("TradeID", "l");
                    MDEntriesOutGroup.setString("MDEntryBuyer", "m");
                    MDEntriesOutGroup.setString("MDEntrySeller", "n");
                    MDEntriesOutGroup.setInteger("NumberOfOrders", 5);
                    MDEntriesOutGroup.setInteger("MDEntryPositionNo", 7);
                    MDEntriesOutGroup.setInteger("PriceType", 8);
                    MDEntriesOutGroup.setDecimal("NetChgPrevDay", 9);
                    MDEntriesOutGroup.setInteger("SellerDays", 10);
                    MDEntriesOutGroup.setInteger("SettlPriceType", 11);
                    MDEntriesOutGroup.setDecimal("TradeVolume", 12);
                    MDEntriesOutGroup.setInteger("PriceLimitType", 13);
                    MDEntriesOutGroup.setDecimal("LowLimitPrice", 14);
                    MDEntriesOutGroup.setDecimal("HighLimitPrice", 15);
                    MDEntriesOutGroup.setDecimal("TradingReferencePrice", 16);
                    MDEntriesOutGroup.setLong("MDEntryID", 17);
                    MDEntriesOutGroup.setInteger("MDInsertDate", 18);
                MDEntriesSequenceValue.add(MDEntriesOutGroup);
            outObj.setFieldValue("MDEntries", MDEntriesSequenceValue);
        messageOut.writeMessage(outObj);
    }
}
