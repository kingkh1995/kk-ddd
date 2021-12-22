-- 秒杀redis脚本
-- redis集群下执行脚本时，需要对KEYS数组都添加同一个hashtag，预先检查键都映射到同一个slot才允许执行。
local key = KEYS[1]
local lkey = key .. 'limit'
local ukey = key .. 'user'
local ckey = key .. 'context'
-- hashkey由key和id拼接而成
local hashkey = KEYS[2]
local number = tonumber(ARGV[1])
local context = ARGV[2]
local stock = tonumber(redis.call('GET', key))
local limit = tonumber(redis.call('GET', lkey))
if stock == nil then
    -- 未开始秒杀
    return nil
elseif number > stock then
    -- 库存数量不足
    return 0
elseif number > limit then
    -- 单次秒杀数量超限制
    return -limit
else
    -- 库存充足
    local owned = tonumber(redis.call('HGET', ukey, hashkey))
    if owned == nil or owned + number <= limit then
        -- 秒杀成功，返回秒杀成功总数
        redis.call('DECRBY', key, number)
        redis.call('LPUSH', ckey, context)
        return redis.call('HINCRBY', ukey, hashkey, number)
    else
        -- 秒杀失败，秒杀成功总数已达限制
        return -limit
    end
end