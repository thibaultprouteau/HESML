import sys

from bert_serving.client import BertClient
from bert_serving.server.helper import get_args_parser
from bert_serving.server import BertServer
import tensorflow as tf
import logging
logger = tf.get_logger()
logger.setLevel(logging.ERROR)

strPoolingStrategy = sys.argv[1]
strPoolingLayer = sys.argv[2]
strModelPath = sys.argv[3]
absPathTempSentencesFile = sys.argv[4] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[5] # the output path

# The pooling layer is modified from "-2,-1" to "-2 -1"

strPoolingLayer = " ".join(strPoolingLayer.split(","))

## INIT THE SERVER CODE ##

args = get_args_parser().parse_args([
                                 '-pooling_strategy', strPoolingStrategy,
                                 '-pooling_layer', strPoolingLayer,
                                 '-model_dir', strModelPath,
                                 '-port', '5555',
                                 '-port_out', '5556',
                                # '-max_seq_len', 'NONE',
                                # '-mask_cls_sep',
                                #  '-verbose', 'True',
                                #  '-http_max_connect', '1000',
                                 '-num_worker', '5',
                                 '-cpu'])
server = BertServer(args)
server.start()

## INIT THE CLIENT CODE ##

bc = BertClient(port=5555)

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if not row == "":
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip().split(" ")
            s2 = data[1].strip().split(" ")

            # infer the vectors of the sentences

            doc_vecs = bc.encode([s1, s2], is_tokenized=True)
            v1 = doc_vecs[0]
            v2 = doc_vecs[1]

            a = v1.tolist()  # nested lists with same data, indices
            b = v2.tolist()  # nested lists with same data, indices

            # format and write the output

            strVector1 = ",".join(map(str, a))
            strVector2 = ",".join(map(str, b))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)

bc.close()
server.close()
print("SCRIPTOK")