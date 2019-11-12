import sys

from bert_serving.client import BertClient
from bert_serving.server.helper import get_args_parser
from bert_serving.server import BertServer
import tensorflow as tf
import logging
logger = tf.get_logger()
logger.setLevel(logging.ERROR)

strModelPath = sys.argv[1]
absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

# strModelPath = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/BertPretrainedModels/NCBI_BERT_pubmed_uncased_L-12_H-768_A-12"
# absPathTempSentencesFile = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/tempSentences.txt"
# absPathTempVectorsFile = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/tempVecs.txt"

## INIT THE SERVER CODE ##

args = get_args_parser().parse_args(['-model_dir', strModelPath,
                                 '-port', '5555',
                                 '-port_out', '5556',
                                # '-max_seq_len', 'NONE',
                                # '-mask_cls_sep',
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

server.close()
print("SCRIPTOK")