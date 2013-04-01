
db.mdb.ensureIndex({deal_id: 1, brand_id: 1}, {unique: true})
db.mdbGroup.ensureIndex({deal_id: 1}, {unique: true})

// 处理mdb数据
db.tmp_subdeals.find().forEach(function(d){
  var mdb = db.mdb.findOne({
    deal_id: d.deal_id - 1500000000,
    brand_id: d.brand_id - 1500000000
  })
  // mdb数据存在，处于发布状态，且内容不为空的使用mdb数据；否则根据subdeal的名称和图片生成默认内容
  if (mdb && mdb.publish && mdb.sections && mdb.sections.length > 0) {
    mdb._id = {
      deal_id: d.deal_id,
      brand_id: d.brand_id
    }
    mdb.deal_id = d.deal_id
    mdb.brand_id = d.brand_id
    mdb.sub_deal_id = d._id
    mdb.mall_id = d.mall_id
    mdb.version = ver
    db.mdbR.save(mdb)
  } else {
    // 根据subdeal的名称和图片生成默认内容
    var sections = [
        {
          type: 'text',
          data: {
            text: d.description.trim() || d.title.trim()
          }
        }
    ]
    if (d.image) {
        sections.push(
          {
            type: 'image',
            data: {
              src: d.image
            }
          }
        )
    }
    db.mdbR.save({
      _id: {
        deal_id: d.deal_id,
        brand_id: d.brand_id
      },
      deal_id: d.deal_id,
      brand_id: d.brand_id,
      sub_deal_id: d._id,
      mall_id: d.mall_id,
      version: ver,
      sections: sections
    })
  }
})

var mall_deals = {}

db.tmp_subdeals.find().forEach(function(subdeal){
  var deal = mall_deals[subdeal.deal_id] = mall_deals[subdeal.deal_id] || {
    _id: subdeal.deal_id,
    deal_id: subdeal.deal_id,
    mall_id: subdeal.mall_id,
    version: ver
  }
  if (!deal.group) {
    var mdbGroup = db.mdbGroup.findOne({
      deal_id: subdeal.deal_id - 1500000000
    })
    deal.group = 'auto'
    if (mdbGroup && mdbGroup.group == 'manual') {
      deal.group = 'manual'
      deal.groups = mdbGroup.groups.map(function(g){
        g.members = g.members.map(function(brand_id){
            return brand_id + 1500000000
        })
        return g
      })
    }
  }
  if (deal.group == 'auto') {
    deal.groups = deal.groups || {}
    var floor = deal.groups[subdeal.floor_id] = deal.groups[subdeal.floor_id] || {
       title: subdeal.floor_name,
       order: subdeal.floor_order,
       members: []
    }
    floor.members.push(subdeal.brand_id)
  }
})

for (deal_id in mall_deals) {
  var deal = mall_deals[deal_id]
  if (deal.group == 'auto') {
    var groupArray = []
    for (floor_id in deal.groups) {
      groupArray.push(deal.groups[floor_id])
    }
    groupArray.sort(function(f1, f2){
      return f1.order - f2.order
    })
    deal.groups = groupArray
  }
  deal.groups.forEach(function(group){
    group.members = group.members.map(function(m){
      return {
        brand: db.tmp_brands.findOne(
          {_id: m},
          {_id: 1, rank: 1}
        ),
        subdeal: db.tmp_subdeals.findOne({
          deal_id: deal._id,
          brand_id: m
        })
      }
    }).filter(function(bs){
      return bs.brand != null && bs.subdeal != null
    }).sort(function(b1, b2){
      return b2.rank - b1.rank
    }).map(function(bs){
      return {
        brand_id: bs.brand._id,
        sub_deal_id: bs.subdeal._id
      }
    })
  })
  deal.groups = deal.groups.filter(function(g){
    return g.members.length != 0
  })
  db.mdbGroupR.save(deal)
}

db.tmp_mall_deals.find().forEach(function(mDeal){
  alreadyThere = db.mdbGroupR.findOne({
      deal_id: mDeal._id,
      mall_id: mDeal.mall_id,
      version: ver
  })
  if (!alreadyThere) {
      db.mdbGroupR.save({
          _id: mDeal._id,
          deal_id: mDeal._id,
          mall_id: mDeal.mall_id,
          version: ver
      })
  }
})

// 删除过期数据
db.mdbR.remove({version: {$lt: ver}}, false)
db.mdbGroupR.remove({version: {$lt: ver}}, false)
