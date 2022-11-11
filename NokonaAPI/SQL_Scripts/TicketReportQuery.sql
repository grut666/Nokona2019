USE nokona;

SELECT th.Key, td.Sequence, th.JobID, td.StatusDate, th.Quantity, td. OperationDescription
FROM ticketheader AS th
	LEFT JOIN ticketdetail AS td ON th.key = td.key
WHERE th.Status = 'P'
	AND td.Status = 'C'
GROUP BY th.Key
HAVING MAX(td.Sequence)
ORDER BY th.JobID, th.Key, td.Sequence;