import sent2vec
import sys

strModelPath = sys.argv[1]
absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

model = sent2vec.Sent2vecModel()
# model.load_model('/home/alicia/Desktop/HESML/HESML_Library/SentenceEmbeddings/BioSentVec_PubMed_MIMICIII-bigram_d700.bin')
# model.load_model('/home/alicia/Desktop/HESML/HESML_Library/SentenceEmbeddings/torontobooks_unigrams.bin')
model.load_model(strModelPath)

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if not row == "":
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip()
            s2 = data[1].strip()

            # infer the vectors of the sentences

            messages = [s1, s2]

            message_embeddings = model.embed_sentences(messages)

            v1 = message_embeddings[0].numpy()
            v2 = message_embeddings[1].numpy()

            # format and write the output

            strVector1 = ",".join(map(str, v1))
            strVector2 = ",".join(map(str, v2))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)

model.release_shared_mem()

print("SCRIPTOK")
