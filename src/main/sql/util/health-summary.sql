-- This automatically generates "Unknown"
select
  if(n.health_status_id is null, 'Unknown', hs.name) status,
  if(n.health_status_id is null, 'warning', st.ukey) type,
  count(*) num_nodes
from
  node n
  left outer join health_status hs on n.health_status_id = hs.id
  left outer join status_type st on hs.status_type_id = st.id,
  service_instance si
where
  n.service_instance_id = si.id
  and si.ukey = 'expweb-prod-5551'
group by
  if(n.health_status_id is null, 0, n.health_status_id)
;
