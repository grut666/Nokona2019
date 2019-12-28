# Nokona2019

## Employee 

## Labor Code

## Labels

## Operation

## Model

## Segment - Contains a wrapped SegmentHeader and a list of all SegmentDetail records for a given segmentName
### Get /segments # Get all Segments
#### In
/segments
#### Out
List of all segments

### Get /segments/{segmentName} # Get 1 Segment by its unique identifer, segmentName
#### In
/segments/100I-LH
#### Out
{\
    "segmentHeader": {\
        "deleted": false,\
        "segmentDescription": "10.5 IN, I WEB - LEFT",\
        "segmentName": "100I-LH"\
    },\
    "segmentDetails": [\
        {\
            "sequence": 1,\
            "opCode": "222",\
            "segmentName": "100I-LH"\
        },\
        {\
            "sequence": 2,\
            "opCode": "333",\
            "segmentName": "100I-LH"\
        },\
        {\
            "sequence": 3,\
            "opCode": "C206",\
            "segmentName": "100I-LH"\
        }\
    ]
        
### Get /segments/segmentHeaders # Get all SegmentHeaders
#### In
/segments/segmentHeaders
#### Out
[\
    {\
        "deleted": false,\
        "segmentDescription": "Description",\
        "segmentName": "1"\
    },\
    {\
        "deleted": false,\
        "segmentDescription": "10.5 IN, I WEB - LEFT",\
        "segmentName": "100I-LH"\
    },\
    {\
        "deleted": false,\
        "segmentDescription": "CLASSIC 100, I-WEB - LEFT",\
        "segmentName": "100ICLASSIC-LH"\
    }\
 ]\
        
### Get /segments/segmentHeaders/{segmentName} # Get 1 SegmentHeader by its unique identifer, segmentName
#### In
/segments/segmentHeaders/1175H-LH
#### Out
{\
    "deleted": false,\
    "segmentDescription": "11.75 IN GLOVE H WEB - LEFT",\
    "segmentName": "1175H-LH"\
}\
        
### Get /segments/segmentDetails/{segmentName} # Get all SegmentDetails for a unique identifer, segmentName
#### In
/segments/segmentDetails/100I-LH
#### Out
[\
    {\
        "sequence": 0,\
        "opCode": "111",\
        "segmentName": "100I  LH"\
    },\
    {\
        "sequence": 1,\
        "opCode": "222",\
        "segmentName": "100I-LH"\
    },\
    {\
        "sequence": 2,\
        "opCode": "333",\
        "segmentName": "100I-LH"\
    }\
]\

