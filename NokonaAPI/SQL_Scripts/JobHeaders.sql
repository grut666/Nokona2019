use nokona;
Select JH.JobId,  TRIM(TRAILING '-RH' FROM JH.JobId) JHIDR, TRIM(TRAILING '-RH' FROM JH.JobId) JHIDL from JobHeader JH
join jobDetail JD on JH.JobID = JD.JobId or JH.JHIDR = JD.JobID or JH.JHIDL = JD.JobID; 

-- where JH.JobId = 'BS-1300C-RH';
 
select JH.JobID , trim(TRIM(TRAILING '-LH' FROM TRIM(TRAILING '-RH' FROM JH.jobId))) as JobIdTrimmed, JD.OpCode, JD.Sequence from JobHeader as JH 
join JobDetail as JD on trim(TRIM(TRAILING '-LH' FROM TRIM(TRAILING '-RH' FROM JH.jobId))) = JD.JobId 
where JH.JobId = 'A-1150C-GR-LH';

select JH.JobID , trim(TRIM(TRAILING '-LH' FROM TRIM(TRAILING '-RH' FROM JH.jobId))) as aaa from JobHeader as JH  where JH.JobId = 'A-1150C-GR-LH';

select * from jobheaderdetail where jobid = 'A-1150C-GR-LH';

select TH.Key, TH.JobID, TH.CreatedDate, TH.Status as "Header Status", TH.statusDate, TH.Quantity, TH.Description,
TD.Sequence, TD.OpCode, TD.StatusDate, TD.Status as "Detail Status", TD.StandardQuantity, TD.HourlyRateSAH, TD.BarCodeID,
	TD.LaborRate, TD.UpdatedSequence, TD.ActualQuantity, TD.OperationDescription, TD.LaborDescription, TD.LaborCode
     from ticketheader TH
join TicketDetail TD on TH.Key = TD.Key where TD.Key = 49978 order by TD.Key, TD.Sequence;

select TH.Key, TH.JobID, TH.CreatedDate, TH.Status as "Header Status", TH.statusDate, TH.Quantity, TH.Description,
TD.Sequence, TD.OpCode, TD.StatusDate, TD.Status as "Detail Status", TD.StandardQuantity, TD.HourlyRateSAH, TD.BarCodeID,
	TD.LaborRate, TD.UpdatedSequence, TD.ActualQuantity, TD.OperationDescription, TD.LaborDescription, TD.LaborCode,
    EM.LastName, EM.FirstName, EM.LaborCode, EM.EmpId
     from ticketheader TH
join TicketDetail TD on TH.Key = TD.Key 
join Employee EM on TD.BarCodeID = EM.BarCodeID order by TD.Key, TD.Sequence;
truncate laborcode_log;
select * from ticketheaderdetail where detail_status_date > '2021-05-27' and detail_status_date < '2021-05-30' and Header_Status = 'C';

CREATE VIEW `ticketheaderdetail` AS
select TH.Key, TH.JobID, TH.CreatedDate, TH.Status as "Header_Status", TH.statusDate as "Header_Status_Date", TH.Quantity, TH.Description,
TD.Sequence, TD.OpCode, TD.StatusDate as "Detail_Status_Date", TD.Status as "Detail_Status", TD.StandardQuantity, TD.HourlyRateSAH, TD.BarCodeID,
	TD.LaborRate, TD.UpdatedSequence, TD.ActualQuantity, TD.OperationDescription, TD.LaborDescription, TD.LaborCode,
    EM.LastName, EM.FirstName, EM.EmpId
     from ticketheader TH
join TicketDetail TD on TH.Key = TD.Key 
join Employee EM on TD.BarCodeID = EM.BarCodeID order by TD.Key, TD.Sequence;

describe ticketdetail
;
select * from jobheader where jobid = 'A-MARK-TEST';