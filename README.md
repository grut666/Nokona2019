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

### Get /employee/bykey/{employeeID} # Get 1 employee by their unique numeric DB key
#### In
/employees/bykey/483
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

### Delete /employees/{empID} # Delete 1 employee record by EmpID
#### In
/employees/BAR10

#### Out
{\
    "Success": "200"\
}

### Delete /employees/bykey/{empID} # Delete 1 employee record by numeric key
#### In
/employees/483

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
### Get /operations/bykey/{key} # Get 1 Operation by unique key
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
## Job

### Get /jobs/jobheaders # Get all Job Headers
#### In
/jobs/jobheaders
#### Out
[
 {\
        "jobId": "AMG700K-CW-LH",\
        "description": "WAL/TAN KANG 13.5 IN CLSD WEB, LEFT",\
        "jobType": "B",\
        "key": 5694,\
        "standardQuantity": 10\
    },\
    {\
        "jobId": "AMG700W-CW-LH",\
        "description": "WALNUT 13.5 IN CLSD WEB - LEFT",\
        "jobType": "B",\
        "key": 5689,\
        "standardQuantity": 10\
    },\
    {\
        "jobId": "BB-1175I-LH",\
        "description": "11.75\" BUCKAROO AMG1175 I-WEB\"",\
        "jobType": "B",\
        "key": 5174,\
        "standardQuantity": 10\
    }\
] 
### Get /jobs/{jobId} # Get 1 entire Job (Header and Detail) Object by unique jobId
#### In
/jobs/BFG
#### Out
{\
    "header": {\
        "jobId": "BFG",\
        "description": "LACE BABY GLOVE",\
        "jobType": "B",\
        "key": 5040,\
        "standardQuantity": 10\
    },\
    "details": [\
        {\
            "sequence": 0,\
            "jobId": "BFG",\
            "opCode": "111"\
        },\
        {\
            "sequence": 1,\
            "jobId": "BFG",\
            "opCode": "222"\
        },\
        {\
            "sequence": 2,\
            "jobId": "BFG",\
            "opCode": "GA7"\
        },\
        {\
            "sequence": 3,\
            "jobId": "BFG",\
            "opCode": "GA8"\
        },\
        {\
            "sequence": 4,\
            "jobId": "BFG",\
            "opCode": "GA9"\
        },\
        {\
            "sequence": 5,\
            "jobId": "BFG",\
            "opCode": "GB3"\
        }\
    ]\
}


### Get /jobs/jobheaders/{jobId} # Get 1 Job Header by unique jobId
#### In
/jobs/jobheaders/BC-1200C-RH
#### Out
{\
    "jobId": "BC-1200C-RH",\
    "description": "12\" BUFFALO COMBO CLOSED WEB\"",\
    "jobType": "B",\
    "key": 5253,\
    "standardQuantity": 10\
}
### Get /jobs/jobheaders/bykey/{key} # Get 1 Job Header by unique key
#### In
/jobs/jobheaders/bykey/5565
#### Out
{\
    "jobId": "BC-1150M-LH",\
    "description": "BUFFALO COMBO 11.5 IN MOD TRAP - LEFT",\
    "jobType": "B",\
    "key": 5565,\
    "standardQuantity": 10\
}
### Put /jobs/jobheaders/{jobId} # Update one Job Header Record
#### In
/jobs/jobheaders/BF1125
Body:
{\
    "jobId": "BF1125",\
    "description": "11.25\" Buffalo GLOVE OPEN WEB\"",\
    "jobType": "B",\
    "key": 4304,\
    "standardQuantity": 10\
}
#### Out
{\
    "jobId": "BF1125",\
    "description": "11.25\" BUFFALO GLOVE OPEN WEB\"",\
    "jobType": "B",\
    "key": 4304,\
    "standardQuantity": 10\
}
        
### Post /jobs/jobheaders # Add 1 Job Header Record
#### In
Body:
{\
    "deleted": false,\
    "jobId": "BXXX123",\
    "description": "12.25\" Human Skin GLOVE OPEN WEB\"",\
    "jobType": "B",\
    "key": 0,\
    "standardQuantity": 15\
}

#### Out
{\
    "jobId": "BXXX123",\
    "description": "12.25\" HUMAN SKIN GLOVE OPEN WEB\"",\
    "jobType": "B",\
    "key": 9733,\
    "standardQuantity": 15\
}
### Get /jobs/jobdetails/{jobId} # Get a List of Job Detail records by unique jobId
#### In
/jobs/jobdetails/BC-1200C-RH
#### Out
[\
    {\
        "sequence": 0,\
        "jobId": "BC-1150M-LH",\
        "opCode": "111"\
    },\
    {\
        "sequence": 1,\
        "jobId": "BC-1150M-LH",\
        "opCode": "222"\
    },\
    {\
        "sequence": 2,\
        "jobId": "BC-1150M-LH",\
        "opCode": "333"\
    },\ 
    {\
        "sequence": 38,\
        "jobId": "BC-1150M-LH",\
        "opCode": "I28"\
    }\
]


### Delete /jobs/{jobId} # Delete 1 Job by the unique jobId.  Deletes both header and detail
#### In
jobs/BXXX123

#### Out
{\
    "Success": "200"\
}

### Delete /jobs/bykey/{key} # Delete 1 Job Record by the unique Database key.  Deletes both header and detail
#### In
/jobs/bykey/9733

#### Out
{\
    "Success": "200"\
}



## Segment - Contains a wrapped SegmentHeader and a list of all SegmentDetail records for a given segmentName


