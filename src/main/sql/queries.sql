-- Select service's instances with health percentages
select
  si.ukey 'Service instance',
  count(*) 'Nodes',
  count(case st.ukey when 'success' then 1 end) 'Healthy'
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
