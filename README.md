# Team: Info Seekers

Info Retrieval Assignment 2

### To run the application:

`mvn exec:java -Dexec.mainClass="app.App"`

### For running on virtual machine (vm):

`mvn exec:java -Dexec.mainClass="app.App"`

### For evaluation using trec_eval:

` trec_eval-9.0.7/trec_eval qrels results/results.txt`

### If duplicates in Results file, then:

`rm -r index/`

## Results -

| runid                | EnglishAnalyzerBM25 |
| -------------------- | ------------------- |
| num_q                | 25                  |
| num_ret              | 25000               |
| num_rel              | 2132                |
| num_rel_ret          | 1541                |
| map                  | 0.3300              |
| gm_map               | 0.2531              |
| Rprec                | 0.3676              |
| bpref                | 0.3219              |
| recip_rank           | 0.8902              |
| iprec_at_recall_0.00 | 0.9234              |
| iprec_at_recall_0.10 | 0.6083              |
| iprec_at_recall_0.20 | 0.5020              |
| iprec_at_recall_0.30 | 0.4449              |
| iprec_at_recall_0.40 | 0.3832              |
| iprec_at_recall_0.50 | 0.3325              |
| iprec_at_recall_0.60 | 0.2603              |
| iprec_at_recall_0.70 | 0.1933              |
| iprec_at_recall_0.80 | 0.1283              |
| iprec_at_recall_0.90 | 0.0767              |
| iprec_at_recall_1.00 | 0.0085              |
| P_5                  | 0.6640              |
| P_10                 | 0.5680              |
| P_15                 | 0.5200              |
| P_20                 | 0.4980              |
| P_30                 | 0.4573              |
| P_100                | 0.2948              |
| P_200                | 0.2010              |
| P_500                | 0.1075              |
| P_1000               | 0.0616              |
