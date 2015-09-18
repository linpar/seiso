-- Select service's instances with health percentages
select
  si.ukey 'service instance',
  count(*) 'nodes',
  count(case when st.ukey in ('info', 'success') then 1 end) 'Healthy'
from
  service s,
  service_instance si,
  node n,
  health_status hs,
  status_type st
where
  s.ukey = 'service-key-here'
  and si.service_id = s.id
  and n.service_instance_id = si.id
  and n.health_status_id = hs.id
  and hs.status_type_id = st.id
group by
  si.id
  ;

-- Global "what's broken" view
select
  *,
  healthy / nodes 'percent healthy'
from (
  select
    si.ukey 'service instance',
    count(*) 'nodes',
    count(case when st.ukey in ('info', 'success') then 1 end) 'healthy'
  from
    service_instance si,
    node n,
    health_status hs,
    status_type st
  where
    n.service_instance_id = si.id
    and n.health_status_id = hs.id
    and hs.status_type_id = st.id
  group by
    si.id) c
order by
  healthy / nodes
    ;

-- Select service instances with service and port info
select
  s.name 'service',
  si.ukey 'service_instance',
  sip.number 'port',
  sip.protocol 'protocol'
from
  service s
  left outer join service_instance si on si.service_id = s.id
  left outer join service_instance_port sip on sip.service_instance_id = si.id
order by
  s.name,
  si.ukey,
  sip.number
;
