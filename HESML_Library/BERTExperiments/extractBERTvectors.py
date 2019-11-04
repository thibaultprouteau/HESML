import codecs
import json
import os

from bert_serving.client import BertClient
from bert_serving.server import BertServer
from bert_serving.server.helper import get_args_parser
from bert_serving.server import BertServer
import tensorflow as tf
import logging
logger = tf.get_logger()
logger.setLevel(logging.ERROR)

# @todo GENERALIZE TO: (1) create output dirs for the input models (2) given a model, get the embeddings for all the input datasets in the preprocessed directory

# bert-serving-start -model_dir=/home/alicia/Desktop/BERT/PretrainedModels/NCBI_BERT_pubmed_uncased_L-12_H-768_A-12 -pooling_layer -4 -3 -2 -1
# bert-serving-start -model_dir=/home/alicia/Desktop/BERT/PretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12
# bert-serving-start -model_dir=/home/alicia/Desktop/BERT/PretrainedModels/NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16
# bert-serving-start -model_dir=/home/alicia/Desktop/BERT/PretrainedModels/NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16

models = [
    "NCBI_BERT_pubmed_uncased_L-12_H-768_A-12",
    "NCBI_BERT_pubmed_mimic_uncased_L-12_H-768_A-12",
    "NCBI_BERT_pubmed_uncased_L-24_H-1024_A-16",
    "NCBI_BERT_pubmed_mimic_uncased_L-24_H-1024_A-16"
]

datasets = [
    "BIOSSESNormalized.tsv",
    "MedStsFullNormalized.tsv",
    "MedStsTestNormalized.tsv",
    "CTRNormalized_averagedScore.tsv"
]

for model in models:


    ## START SERVER  for the model ##

    model_path = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/BertPretrainedModels/" + model
    args = get_args_parser().parse_args(['-model_dir', model_path,
                                         '-port', '5555',
                                         '-port_out', '5556',
                                         '-max_seq_len', 'NONE',
                                         '-mask_cls_sep',
                                         '-cpu'])
    server = BertServer(args)
    server.start()

    ## INIT THE CLIENT CODE ##

    bc = BertClient(port=5555)

    # open the dataset files
    # open the json input file with each row is a dict {idSentence, sentencesTokenized}

    for dataset in datasets:
        dataset_normalized = dataset.replace(".", "")
        output_path = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/generatedEmbeddings/" + dataset_normalized + "/vectors_" + model
        output_file = output_path +  "/embeddings.json"

        os.makedirs(output_path)

        datasets_path = "/home/alicia/Desktop/HESML/HESML_Library/SentenceSimDatasets/preprocessedDatasets/" + dataset
        f = open(datasets_path, "r")
        file = f.read().split("\n")

        count = 1

        output = {}
        for row in file:
            if not row == "":
                data = row.split("\t")

                # get the sentences

                # s1 = tokenize_ncbi_bert_sentence(data[0].strip())
                # s2 = tokenize_ncbi_bert_sentence(data[1].strip())
                s1 = data[0].strip().split(" ")
                s2 = data[1].strip().split(" ")


                # get the vectors of the sentences

                doc_vecs = bc.encode([s1, s2], is_tokenized=True)
                v1 = doc_vecs[0]
                v2 = doc_vecs[1]


                # convert vectors to strings

                # str_v1 = np.array2string(v1, separator=',', threshold=1000000)
                # str_v2 = np.array2string(v2, separator=',', threshold=1000000)

                a = v1.tolist()  # nested lists with same data, indices
                b = v2.tolist()  # nested lists with same data, indices


                # output[count] = [a, b]
                # write into file
                output[data[0].strip()] = a
                output[data[1].strip()] = b

                # the_file.write(str(count) + "\t" + str_v1 + "\t" + str_v2 + "\t" + score + "\n")
                count = count+1

        json.dump(output, codecs.open(output_file, 'w', encoding='utf-8'), separators=(',', ':'), sort_keys=False, indent=4)

    server.close()
