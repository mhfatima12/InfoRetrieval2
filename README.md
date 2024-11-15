# CS7IS3-Assignment-2
Group Project for CS7IS3 Assignment 2


### To run the application:
`java -jar target/SearchEngine-1.0.jar`

### For running on virtual machine:

`java -Xmx4G -cp "target/SearchEngine-1.0.jar" app.App`

### For evaluation using trec_eval:
`trec_eval/trec_eval ~/qrels results/results.txt`

### If duplicates in Results file, then:
`rm -r index/`

## Results - 


| runid                | EnglishAnalyzerBM25 |
| -------------------- |:-------------------:|
| num_q                | 25                  |
| num_ret              | 25000               |
| num_rel              | 2132                |
| num_rel_ret          | 1538                |
| map                  | 0.3302              |
| gm_map               | 0.2514              |
| Rprec                | 0.3686              |
| bpref                | 0.3227              |
| recip_rank           | 0.8902              |
| iprec_at_recall_0.00 | 0.9237              |
| iprec_at_recall_0.10 | 0.6074              |
| iprec_at_recall_0.20 | 0.5047              |
| iprec_at_recall_0.30 | 0.4450              |
| iprec_at_recall_0.40 | 0.3859              |
| iprec_at_recall_0.50 | 0.3320              |
| iprec_at_recall_0.60 | 0.2627              |
| iprec_at_recall_0.70 | 0.1923              |
| iprec_at_recall_0.80 | 0.1277              |
| iprec_at_recall_0.90 | 0.0770              |
| iprec_at_recall_1.00 | 0.0086              |
| P_5                  | 0.6640              |
| P_10                 | 0.5680              |
| P_15                 | 0.5227              |
| P_20                 | 0.4960              |
| P_30                 | 0.4560              |
| P_100                | 0.2956              |
| P_200                | 0.2020              |
| P_500                | 0.1073              |
| P_1000               | 0.0615              |



### Stopwords:
http://www.lextek.com/manuals/onix/stopwords1.html

