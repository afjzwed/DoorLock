package com.cxwl.hurry.doorlock.utils;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

/**
 * Nfc回调类,nfc卡扫描时调用
 * Callback class, invoked when an NFC card is scanned while the device is running in reader mode.
 * Reader mode can be invoked by calling NfcAdapter
 *
 * Created by William on 2018/5/8.
 */
@TargetApi(19)
public class NfcReader implements NfcAdapter.ReaderCallback  {
    public static final String ACTION_NFC_CARDINFO="com.example.cts.textnfc.cardinfo";
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private AccountCallback accountCallback;

    public NfcReader(AccountCallback accountCallback) {
        this.accountCallback = accountCallback;
    }

    /**
     * NFC读卡
     * @param tag Discovered tag
     */
    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        byte[] newBuffer=tag.getId();
        String cardId=RfidUtil.convertToCardNo(newBuffer,4);
        if(accountCallback!=null){
            accountCallback.onAccountReceived(cardId);
        }
    }

    @TargetApi(19)
    public interface AccountCallback {
        public void onAccountReceived(String account);
    }
}
