use nokona;
select * from ticketheader where jobid like 'Z%' and statusdate < '2022-02-01';
select * from ticketheader where jobid like 'Z%' and status = 'C' order by ticketheader.key;
select * from ticketheader where jobid like 'Y%' and statusdate < '2022-02-01';
