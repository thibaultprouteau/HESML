import sys
from bert import tokenization

# Get the arguments
m_vocab_file = sys.argv[1]
sentence = sys.argv[2]

# print(sys.argv)
m_do_lower_case = True
# m_vocab_file = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/PretrainedModels/BioBERT/biobert_v1.0_pmc/vocab.txt"
# absPathTempSentencesFile = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/tempSentences.txt"
# absPathTempWordPieceTokenizedFile = "/home/alicia/Desktop/HESML/HESML_Library/BERTExperiments/tempWordPieceTokenized.txt"

# Tokenize and print the tokenized sentence

tokenizer = tokenization.FullTokenizer(vocab_file=m_vocab_file, do_lower_case=m_do_lower_case)

tokens_sentence_1 = tokenizer.tokenize(sentence)
new_line_sentence_1 = " ".join(tokens_sentence_1)

print(new_line_sentence_1.rstrip())
