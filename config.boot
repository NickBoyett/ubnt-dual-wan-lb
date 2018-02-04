firewall {
    all-ping enable
    broadcast-ping disable
    group {
        address-group ATT_FIRST {
            /* teamspeak ip */
            address 74.91.112.83
        }
        address-group GOOGLE_FIRST {
            /* ovh ips */
            address 103.5.12.0/22
            address 137.74.0.0/16
            address 139.99.0.0/17
            address 139.99.128.0/17
            address 142.4.192.0/19
            address 142.44.128.0/17
            address 144.217.0.0/16
            address 145.239.0.0/16
            address 147.135.0.0/17
            address 147.135.128.0/17
            address 149.202.0.0/16
            address 149.56.0.0/16
            address 151.80.0.0/16
            address 158.69.0.0/16
            address 164.132.0.0/16
            address 167.114.0.0/17
            address 167.114.128.0/18
            address 167.114.192.0/19
            address 167.114.224.0/19
            address 176.31.0.0/16
            address 176.31.176.0/22
            address 176.31.184.0/22
            address 176.31.188.0/22
            address 178.32.0.0/15
            address 178.32.134.0/24
            address 178.32.135.0/24
            address 185.12.32.0/23
            address 188.165.0.0/16
            address 192.95.0.0/18
            address 192.99.0.0/16
            address 193.104.19.0/24
            address 193.109.63.0/24
            address 193.70.0.0/17
            address 195.110.30.0/23
            address 195.246.232.0/23
            address 198.100.144.0/20
            address 198.245.48.0/20
            address 198.27.64.0/18
            address 198.27.92.0/24
            address 198.50.128.0/17
            address 213.186.32.0/19
            address 213.251.128.0/18
            address 213.32.0.0/17
            address 217.182.0.0/16
            address 37.187.0.0/16
            address 37.59.0.0/16
            address 37.60.48.0/21
            address 37.60.56.0/21
            address 46.105.0.0/16
            address 46.105.192.0/20
            address 46.105.192.0/23
            address 46.105.194.0/23
            address 46.105.196.0/23
            address 46.105.198.0/24
            address 51.254.0.0/15
            address 5.135.0.0/16
            address 5.196.0.0/16
            address 5.39.0.0/17
            address 54.36.0.0/16
            address 54.37.0.0/16
            address 54.38.0.0/16
            address 54.39.0.0/16
            address 62.245.0.0/19
            address 66.70.128.0/17
            address 79.137.0.0/18
            address 79.137.64.0/18
            address 8.18.128.0/24
            address 8.18.136.0/21
            address 8.18.172.0/24
            address 8.20.110.0/24
            address 8.21.41.0/24
            address 8.24.8.0/21
            address 8.26.94.0/24
            address 8.29.224.0/24
            address 8.30.208.0/21
            address 8.33.128.0/21
            address 8.33.136.0/24
            address 8.33.137.0/24
            address 8.33.96.0/21
            address 8.7.244.0/24
            address 87.98.128.0/17
            address 91.121.0.0/16
            address 91.134.0.0/16
            address 91.90.88.0/21
            address 92.222.0.0/16
            address 94.23.0.0/16
        }
        network-group PRIVATE_NETS {
            network 192.168.0.0/16
            network 172.16.0.0/12
            network 10.0.0.0/8
        }
    }
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians disable
    modify balance {
        rule 10 {
            action modify
            description "do NOT load balance lan to lan"
            destination {
                group {
                    network-group PRIVATE_NETS
                }
            }
            modify {
                table main
            }
        }
        rule 30 {
            action modify
            description "do NOT load balance destination public address"
            destination {
                group {
                    address-group ADDRv4_eth1
                }
            }
            modify {
                table main
            }
        }
        rule 40 {
            action modify
            description "do NOT load balance destination public address"
            destination {
                group {
                    address-group ADDRv4_eth2
                }
            }
            modify {
                table main
            }
        }
        rule 50 {
            action modify
            destination {
                group {
                    address-group ATT_FIRST
                }
            }
            modify {
                lb-group ATT_FIRST_G
            }
        }
        rule 60 {
            action modify
            modify {
                lb-group GOOGLE_FIRST_G
            }
            source {
                group {
                    address-group GOOGLE_FIRST
                }
            }
        }
        rule 110 {
            action modify
            modify {
                lb-group G
            }
        }
    }
    name WAN_IN {
        default-action accept
        description "WAN to internal"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        address 192.168.0.1/24
        description Local
        duplex auto
        firewall {
            in {
                modify balance
            }
        }
        speed auto
    }
    ethernet eth1 {
        address dhcp
        description GOOGLE
        duplex auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
        speed auto
    }
    ethernet eth2 {
        address dhcp
        description ATT
        duplex auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
        speed auto
    }
    loopback lo {
    }
}
load-balance {
    group ATT_FIRST_G {
        interface eth1 {
            failover-only
        }
        interface eth2 {
        }
        lb-local enable
        lb-local-metric-change disable
    }
    group G {
        interface eth1 {
        }
        interface eth2 {
        }
        lb-local enable
        lb-local-metric-change disable
    }
    group GOOGLE_FIRST_G {
        interface eth1 {
        }
        interface eth2 {
            failover-only
        }
        lb-local enable
        lb-local-metric-change disable
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN {
            authoritative enable
            subnet 192.168.0.0/24 {
                default-router 192.168.0.1
                dns-server 192.168.0.1
                lease 86400
                start 192.168.0.38 {
                    stop 192.168.0.243
                }
            }
        }
        use-dnsmasq disable
    }
    dns {
        forwarding {
            cache-size 150
            listen-on eth0
        }
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 5002 {
            description "masquerade for WAN"
            outbound-interface eth1
            type masquerade
        }
        rule 5004 {
            description "masquerade for WAN 2"
            outbound-interface eth2
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    conntrack {
        expect-table-size 4096
        hash-size 4096
        table-size 32768
        tcp {
            half-open-connections 512
            loose enable
            max-retrans 3
        }
    }
    host-name ubnt
    login {
        user ubnt {
            authentication {
                encrypted-password "REDACTED"
            }
            level admin
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-unms@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.9.7+hotfix.4.5024004.171005.0403 */
