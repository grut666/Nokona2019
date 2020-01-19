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
]

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
}
        
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
}
#### Out
{\
    "lastName": "Williams",\
    "firstName": "BRANDY",\
    "barCodeID": 6593,\
    "empId": "BAR10",\
    "laborCode": 11,\
    "active": false,\
    "key": 483\
}
        
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
}
#### Out
{\
    "lastName": "MORALES",\
    "firstName": "JUSTIN",\
    "barCodeID": 1234,\
    "empId": "JUS66",\
    "laborCode": 12,\
    "active": true,\
    "key": 6789\
}

### Delete /employees/{empID} # Delete 1 employee record
#### In
/employees/BAR10

#### Out
{\
    "Success": "200"\
}

## Labor Code - Describes one Labor Code Record

### Get /laborcodes # Get all Labor Codes
#### In
/laborcodes
#### Out
[\
    {
        "rate": 10.75,\
        "laborCode": 1,\
        "description": "EMBROIDERY",\
        "key": 33\
    },\
    {\
        "rate": 8.0,\
        "laborCode": 2,\
        "description": "GLUE TABLE~ GREASE",\
        "key": 2\
    },\
    {\
        "rate": 11.0,\
        "laborCode": 3,\
        "description": "STAMPING",\
        "key": 3\
    }
]

### Get /laborcodes/{laborCode} # Get 1 Labor Code by the unique LaborCode
#### In
/laborcodes/3
#### Out
{\
    "rate": 11.0,\
    "laborCode": 3,\
    "description": "STAMPING",\
    "key": 3\
}

### Get /laborcodes/bykey/{key} # Get 1 Labor Code by the unique Database Key
#### In
/laborcodes/bykey/30
#### Out
{\
    "rate": 8.0,\
    "laborCode": 16,\
    "description": "LAY OFF GLOVES",\
    "key": 30\
}
        
### Put /laborcodes/{laborCode} # Update one Labor Code Record
#### In
/laborcodes/16
Body:
{\
    "rate": 12.0,\
    "laborCode": 16,\
    "description": "LAYOFF GLOVES",\
    "key": 30\
}
#### Out
{\
    "rate": 12.0,\
    "laborCode": 16,\
    "description": "LAYOFF GLOVES",\
    "key": 30\
}
        
### Post /laborcodes # Add 1 Labor Code Record
#### In
Body:

{\
    "rate": 9.0,\
    "laborCode": 66,\
    "description": "Do Some Cool Stuff",\
    "key": 0\
}
#### Out
{\
    "rate": 9.0,\
    "laborCode": 66,\
    "description": "Do Some Cool Stuff",\
    "key": 40\
}

### Delete /laborcodes/{laborCode} # Delete 1 Labor Code Record by the unique laborCode
#### In
/laborcodes/66

#### Out
{\
    "Success": "200"\
}

### Delete /laborcodes/bykey/{key} # Delete 1 Labor Code Record by the unique Database key
#### In
/laborcodes/bykey/40

#### Out
{\
    "Success": "200"\
}
## Labels

## Operation

### Get /operations # Get all Operations
#### In
/operations
#### Out
[
{\
        "lastStudyYear": 0,\
        "hourlyRateSAH": 0.0036,\
        "description": "SPLIT LINING                  CATCHERS MITT                 ",\
        "laborCode": 7,\
        "active": false,\
        "opCode": "Z97",\
        "key": 12791\
    },\
    {\
        "lastStudyYear": 0,\
        "hourlyRateSAH": 0.04,\
        "description": "STAMP PALM                    CATCHERS MITT                 ",\
        "laborCode": 7,\
        "active": false,\
        "opCode": "Z98",\
        "key": 12792\
    },\
    {\
        "lastStudyYear": 0,\
        "hourlyRateSAH": 0.06,\
        "description": "STAMP MITT                                                  ",\
        "laborCode": 7,\
        "active": false,\
        "opCode": "ZA3",\
        "key": 12796\
    }\
] 

### Get /operations/{opcode} # Get 1 Operation by unique opCode
#### In
/operations/Z98
#### Out
{\
        "lastStudyYear": 0,\
        "hourlyRateSAH": 0.04,\
        "description": "STAMP PALM                    CATCHERS MITT                 ",\
        "laborCode": 7,\
        "active": false,\
        "opCode": "Z98",\
        "key": 12792\
}
### Get /operations/bykey/{opcode} # Get 1 Operation by unique key
#### In
/operations/bykey/13315
#### Out
{\
    "lastStudyYear": 0,\
    "hourlyRateSAH": 0.0033,\
    "description": "CLICK GENUINE BLACK SHEEP - WRIST",\
    "laborCode": 7,\
    "active": false,\
    "opCode": "B21B",\
    "key": 13315\
}
### Put /operations/{opCode} # Update one Operation Record
#### In
/operations/B21B
Body:
{\
    "lastStudyYear": 0,\
    "hourlyRateSAH": 0.0036,\
    "description": "CLICK GENUINE WHITE SHEEP - WRIST",\
    "laborCode": 7,\
    "active": false,\
    "opCode": "B21B",\
    "key": 13315\
}
#### Out
{\
    "lastStudyYear": 0,\
    "hourlyRateSAH": 0.0036,\
    "description": "CLICK GENUINE WHITE SHEEP - WRIST",\
    "laborCode": 7,\
    "active": false,\
    "opCode": "B21B",\
    "key": 13315\
}
        
### Post /operations # Add 1 Operation Record
#### In
Body:
{\
    "lastStudyYear": 0,\
    "hourlyRateSAH": 0.0033,\
    "description": "TEST Operation",\
    "laborCode": 7,\
    "active": false,\
    "opCode": "XYZ2",\
    "key": 0\
}

#### Out
{\
    "lastStudyYear": 0,\
    "hourlyRateSAH": 0.0033,\
    "description": "TEST Operation",\
    "laborCode": 7,\
    "active": false,\
    "opCode": "XYZ2",\
    "key": 13343\
}

### Delete /operations/{opCode} # Delete 1 Operation Record by the unique opCode
#### In
operations/XYZ1

#### Out
{\
    "Success": "200"\
}

### Delete /operations/bykey/{key} # Delete 1 Operation Record by the unique Database key
#### In
/operations/bykey/13315

#### Out
{\
    "Success": "200"\
}
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

