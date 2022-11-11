use nokona;
select jobid from ticketheader where jobid like 'Z_A%' and createdDate < '2021-02-01';
select jobid from jobheader where jobid like 'Z_AL%'; and createdDate < '2021-02-01';
select distinct(jobid) from ticketheader where jobid like 'Z_AL%' and createdDate < '2021-02-01';

delete from jobheader where jobid in (select distinct(jobid) from ticketheader where jobid like 'Z_AL%' and createdDate < '2021-02-01');

delete from jobheader where jobid in ('Z_AUBREY');
delete from jobdetail where jobid in ('Z_AUBREY');