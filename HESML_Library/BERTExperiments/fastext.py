# import fasttext

# model = fasttext.load_model("/home/alicia/OneDrive/DatasetEcienciaDatos/CBOWfastext/bioCFastextCbowTrained.bin")
#
# words = model.get_words()
#
# print(len(words))
#
# with open('all_words_in_pretrainedCBOWFastext.txt', 'w') as f:
#     for word in words:
#         f.write("%s\n" % word)



filep = '/home/alicia/Desktop/HESML_dependencies_directories/BioCCorpus/allSentencesInAFile.txt'

count_line = 0
with open(filep, 'r') as fp:
   line = fp.readline()

   for line in fp:
       word = line.split(' ', 1)[0]
       if '<' in word:
           print("LINE: " + line)
           print("Countline: " + str(count_line))

       line = fp.readline()

       count_line+=1

print("END")



fp.close()