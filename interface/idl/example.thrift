namespace java com.example
namespace py com.example

enum TransferType {
    REQUEST = 0,
    PROGRESS
}

struct TransferInfo {
    1: TransferType type,
    2: string fileName
    3: i64 length,
    4: binary data
}

service EchoService {
    string echo(1: string input)
}

service DownloadService {
    TransferInfo download(1:TransferInfo info),
    list<string> getFileList()
}

service UploadService {
    void upload(1: TransferInfo info),
}