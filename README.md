# Nokona2019

## Employee - Describes one employee

### Get /employees # Get all Employees
#### In
/employees
#### Out
[\
    {\
        "lastName": "ALMAZAN",\
        "firstName": "MONICA",\
        "barCodeID": 9297,\
        "empId": "ALM10",\
        "laborCode": 11,\
        "active": false,\
        "key": 441\
    },\
    {\
        "lastName": "ALONSO",\
        "firstName": "MARIA",\
        "barCodeID": 2749,\
        "empId": "ALO10",\
        "laborCode": 11,\
        "active": false,\
        "key": 552\
    },\
    {\
        "lastName": "ALVARADO",\
        "firstName": "MARIA",\
        "barCodeID": 6756,\
        "empId": "ALV10",\
        "laborCode": 5,\
        "active": false,\
        "key": 316\
    }\
]\

### Get /employee/{employeeID} # Get 1 employee by their unique identifer, employeeID
#### In
/employees/BAR10
#### Out
{\
    "lastName": "BARKER",\
    "firstName": "BRANDY",\
    "barCodeID": 6593,\
    "empId": "BAR10",\
    "laborCode": 11,\
    "active": false,\
    "key": 483\
}\
        
### Put /employees/{empID} # Update one employee
#### In
/employees/BAR10
Body:
{\
    "lastName": "Williams",\
    "firstName": "BRANDY",\
    "barCodeID": 6593,\
    "empId": "BAR10",\
    "laborCode": 11,\
    "active": false,\
    "key": 483\
}\
#### Out
{\
    "lastName": "Williams",\
    "firstName": "BRANDY",\
    "barCodeID": 6593,\
    "empId": "BAR10",\
    "laborCode": 11,\
    "active": false,\
    "key": 483\
}\
        
### Post /employees # Add 1 employee
#### In
Body:

{\
    "lastName": "MORALES",\
    "firstName": "JUSTIN",\
    "barCodeID": 1234,\
    "empId": "JUS66",\
    "laborCode": 12,\
    "active": true,\
    "key": 0\
}\
#### Out
{\
    "lastName": "MORALES",\
    "firstName": "JUSTIN",\
    "barCodeID": 1234,\
    "empId": "JUS66",\
    "laborCode": 12,\
    "active": true,\
    "key": 6789\
}\

### Delete /employees/{empID} # Add 1 employee
#### In
/employees/BAR10

#### Out
{\
    "Success": "200"\
}\

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
 ]
        
### Get /segments/segmentHeaders/{segmentName} # Get 1 SegmentHeader by its unique identifer, segmentName
#### In
/segments/segmentHeaders/1175H-LH
#### Out
{\
    "deleted": false,\
    "segmentDescription": "11.75 IN GLOVE H WEB - LEFT",\
    "segmentName": "1175H-LH"\
}
        
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
]

