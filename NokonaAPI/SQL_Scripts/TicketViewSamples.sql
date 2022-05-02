Use Nokona;

-- Get Count of tickets by kdey
SELECT count(*) FROM nokona.ticketview where ticketview.key = 49425 order by sequence;

-- Get Count of tickets by JobID that are completed
SELECT count(*) FROM nokona.ticketview where ticketview.JobId = 'W-3350C-LH';

-- Get tickets by JobID that are completed with date range
SELECT * FROM nokona.ticketview where ticketview.JobId = 'W-3350C-LH' and header_status_date between '2022-01-01' and '2022-04-30' order by sequence;

SELECT * FROM nokona.ticketview where JobID = 'W-3350C-LH' and header_status <> 'C' order by ticketview.key, sequence;

-- Get Count of tickets by JobID with date range
select max(ticketview.key) from ticketview;







Select count(*) from TicketHeader;
select count(*) from ticketdetail order by sequence;
select * from jobview;
select sum(hourlyratesah * laborRate * actualquantity), hourlyratesah * laborRate * actualquantity, empid, hourlyRateSAH, LaborRate, ActualQuantity from ticketview 
where EMPID = 'BAS10' and detail_status = 'C' and detail_status_date between '2021-05-01' and '2021-05-31'
group by empid;

select empid as "Emp ID",  format(sum(hourlyratesah *  actualquantity),2) as "Total SAH",  format(sum(hourlyratesah * laborRate * actualquantity), 2) as "Total Cost" from ticketview 
where detail_status = 'C' and detail_status_date between '2021-05-01' and '2021-05-31'
group by empid with rollup
order by EMPID;

select empid as "Emp ID", hourlyratesah, actualquantity, laborrate, opcode, detail_status_date, format((hourlyratesah *  actualquantity),2) as "Total SAH",  format((hourlyratesah * laborRate * actualquantity), 2) as "Total Cost", empid from ticketview 
where empid = 'GUI10' and detail_status = 'C' and detail_status_date between '2021-05-01' and '2021-05-31'
order by detail_status_date, EMPID;

select * from ticketdetail where barcodeID = 489 and statusdate between '2021-05-01' and '2021-05-31'
order by statusdate;
