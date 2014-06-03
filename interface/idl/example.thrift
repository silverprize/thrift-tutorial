namespace java com.example

typedef i32 int
typedef i64 long

enum UploadMessage {
    BEGIN_UPLOAD = 0,
    PROGRESS_UPLOAD
    END_UPLOAD
}

struct UploadInfo {
    1: UploadMessage msg,
    2: string fileName
    3: i64 length,
    4: binary data
}

service ExampleService {
    string echo(1: string input),
    bool upload(1: UploadInfo info)
}