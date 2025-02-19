DELETE
FROM graph
WHERE start_node_id NOT IN (SELECT end_node_id FROM graph)
AND end_node_id NOT IN (SELECT start_node_id FROM graph);

INSERT INTO graph_nodes (node_id, latitude, longitude)
SELECT node_id, lat, lon
FROM nodes
WHERE node_id IN (
    SELECT DISTINCT start_node_id
    FROM graph
    UNION
    SELECT DISTINCT end_node_id
    FROM graph
);

DROP TABLE nodes;