# CS7IS3-Assignment-2
Group Project for CS7IS3 Assignment 2


### To run the application:
`mvn exec:java -Dexec.mainClass="app.App"`

### For running on virtual machine:

`mvn exec:java -Dexec.mainClass="app.App"`

### For evaluation using trec_eval:
`trec_eval-9.0.7/trec_eval ./qrels results/results.txt`

### If duplicates in Results file, then:
`rm -r index/`

## Results - These results are obtained by using the BM25 Similarity along with the expanded query option.


| runid                | CustomAnalyzerBM25  |
| -------------------- |:-------------------:|
| num_q                | 25                  |
| num_ret              | 25000               |
| num_rel              | 2132                |
| num_rel_ret          | 1540                |
| map                  | 0.3321              |
| gm_map               | 0.2745              |
| Rprec                | 0.3743              |
| bpref                | 0.3302              |
| recip_rank           | 0.8608              |
| iprec_at_recall_0.00 | 0.8895              |
| iprec_at_recall_0.10 | 0.6575              |
| iprec_at_recall_0.20 | 0.5622              |
| iprec_at_recall_0.30 | 0.4711              |
| iprec_at_recall_0.40 | 0.3965              |
| iprec_at_recall_0.50 | 0.3350              |
| iprec_at_recall_0.60 | 0.2534              |
| iprec_at_recall_0.70 | 0.1661              |
| iprec_at_recall_0.80 | 0.0973              |
| iprec_at_recall_0.90 | 0.0297              |
| iprec_at_recall_1.00 | 0.0029              |
| P_5                  | 0.6880              |
| P_10                 | 0.6000              |
| P_15                 | 0.5627              |
| P_20                 | 0.5340              |
| P_30                 | 0.4680              |
| P_100                | 0.2936              |
| P_200                | 0.1994              |
| P_500                | 0.1076              |
| P_1000               | 0.0616              |



