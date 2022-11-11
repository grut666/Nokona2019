use nokona;
select ticketheader.key, jobid, statusdate, quantity from ticketheader where statusdate between '2022-04-28' and '2022-04-29' order by ticketheader.key;