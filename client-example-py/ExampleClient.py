import sys

from thrift.Thrift import TException
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket, TTransport

if __name__ == "__main__":
	sys.path.append("../interface/src/main/py")
	from com.example import EchoService, DownloadService
	from com.example.ttypes import TransferInfo, TransferType

	transport = TSocket.TSocket('localhost', 10004)
	try:
		transport = TTransport.TBufferedTransport(transport)
		protocol = TBinaryProtocol.TBinaryProtocol(transport)
		echo_client = EchoService.Client(protocol)
		transport.open()

		print "Echo test:"
		msg = sys.stdin.readline()
		print echo_client.echo(msg)
	except TException as e:
		print e
	finally:
		transport.close()

	transport = TSocket.TSocket('localhost', 10040)
	try:
		transport = TTransport.TBufferedTransport(transport)
		protocol = TBinaryProtocol.TBinaryProtocol(transport)
		download_client = DownloadService.Client(protocol)
		transport.open()

		file_list = download_client.getFileList()
		file_index = 0
		for file_name in file_list:
			print "[" + str(file_index) + "] " + file_name
			file_index += 1

		if len(file_list) > 0:
			print "Select number:"
			selected = sys.stdin.readline()
			reqInfo = TransferInfo()
			reqInfo.type = TransferType.REQUEST
			reqInfo.fileName = file_list[int(selected)]
			result = download_client.download(reqInfo)
			total = result.length
			cur = 0
			reqInfo.type = TransferType.PROGRESS
			downloaded = open(file_list[int(selected)], "w")
			while total > cur:
				result = download_client.download(reqInfo)
				cur += result.length
				downloaded.write(result.data)
			downloaded.close()
			print "Success to download."
	except TException as e:
		print e
	finally:
		transport.close()
