package woyou.aidlservice.jiuiv5;

import woyou.aidlservice.jiuiv5.ICallback;

interface IWoyouService {
    void printerInit(in ICallback callback);
    void printerSelfChecking(in ICallback callback);

    int updatePrinterState();

    void setAlignment(int alignment, in ICallback callback);
    void setFontSize(float fontsize, in ICallback callback);

    void printText(String text, in ICallback callback);
    void printTextWithFont(String text, String typeface, float fontsize, in ICallback callback);

    void printBarCode(String data, int symbology, int height, int width, int textposition, in ICallback callback);
    void printQRCode(String data, int modulesize, int errorlevel, in ICallback callback);

    void lineWrap(int n, in ICallback callback);

    void sendRAWData(in byte[] data, in ICallback callback);

    void enterPrinterBuffer(boolean clean);
    void exitPrinterBufferWithCallback(boolean commit, in ICallback callback);
}
